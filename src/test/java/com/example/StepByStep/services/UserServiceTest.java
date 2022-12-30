package com.example.StepByStep.services;

import com.example.StepByStep.entities.Role;
import com.example.StepByStep.entities.User;
import com.example.StepByStep.repositories.UserRepo;
import org.apache.el.stream.Stream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailSender;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepo repo;

    @MockBean
    private SmtpMailSender mailSender;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    public void loadUserByUsername() {
        Mockito.doReturn(new User()).when(repo).findByUsername("Taras");
        Assert.assertNotNull(userService.loadUserByUsername("Taras"));
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadByUsername_ifUsernameNotFound() {
        Assert.assertNull(userService.loadUserByUsername("Taras"));
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadByUsername_ifUserIsNotActivated() {
        Mockito.doReturn(new User()).when(repo).findByUsername("Taras");
        User user = repo.findByUsername("Taras");
        user.setCode("dsf");
        Assert.assertNull(userService.loadUserByUsername("Taras"));
    }
@Before
    @Test
    public void addUser() {
        User user = new User();
        user.setUsername("Taras");
        user.setPassword("password");
        user.setEmail("taras@gmail.com");

        Assert.assertFalse(userService.addUser(user));
        Assert.assertNotNull(user.getCode());
        Assert.assertTrue(user.getRoles().contains(Role.USER));

        Mockito.verify(repo, Mockito.times(1)).save(user);
        Mockito.verify(mailSender, Mockito.times(1))
                .send(ArgumentMatchers.eq(user.getEmail()), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    public void update() {
        User user = new User();
        user.setUsername("Taras");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRoles(new HashSet<Role>());
        user.getRoles().add(Role.USER);
        user.getRoles().add(Role.ADMIN);

        Map<String, String> form= new TreeMap<>();
        form.put("USER", "USER");
        form.put("ADMIN", "ADMIN");

        Mockito.doReturn(Optional.of(new User())).when(repo).findById(1L);
        User userToBeUpdated = repo.findById(1L).get();
        userToBeUpdated.setRoles(new HashSet<>());
        userService.update(1L, user, form);

        assertEquals(user.getUsername(), repo.findById(1L).get().getUsername());
        assertEquals(user.getPassword(), repo.findById(1L).get().getPassword());
        assertEquals(user.getRoles(), repo.findById(1L).get().getRoles());

        Mockito.verify(repo, Mockito.times(1)).save(ArgumentMatchers.any(User.class));
    }

    @Test
    public void activateUser() {
        User user = new User();
        Mockito.doReturn(user).when(repo).findByCode("code");

        Assert.assertTrue(userService.activateUser("code"));
        Assert.assertTrue(user.isActive());
        Assert.assertNull(user.getCode());

        Mockito.verify(repo,Mockito.times(1)).save(ArgumentMatchers.any(User.class));
    }

    @Test
    public void activateUser_ifUserNotExists(){
        Assert.assertFalse(userService.activateUser("code"));
        Mockito.verify(repo,Mockito.times(0)).save(ArgumentMatchers.any(User.class));
    }

    @Test
    public  void updateBestResult(){
        User user = new User();
        user.setBestResult(2);
        userService.updateBestResult(user, 3);
        Mockito.verify(repo,Mockito.times(1)).save(user);
    }

    @Test
    public  void updateBestResult_ifCountFewerThanBestResult(){
        User user = new User();
        user.setBestResult(2);
        userService.updateBestResult(user, 1);
        Mockito.verify(repo,Mockito.times(0)).save(user);
    }
}