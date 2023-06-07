package com.example.StepByStep.services;

import com.example.StepByStep.entities.Role;
import com.example.StepByStep.entities.User;
import com.example.StepByStep.repositories.UserRepo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.util.*;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SmtpMailSender mailSender;

    private final List<CurrentGame> games = new ArrayList<>();

    private static final Logger LOGGER = LogManager.getLogger(UserService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOGGER.info("Method 'loadUserByUsername' is started\nUsername " + username);
        User userFromDB = userRepo.findByUsername(username);
        if (userFromDB == null) {
            LOGGER.debug("User with username " + username + " not found");
            throw new UsernameNotFoundException("Користувач не знайдений");
        } else if (userFromDB.getCode() != null) {
            LOGGER.debug("User with username " + username + " not activated");
            throw new UsernameNotFoundException("Користувач не активований");
        } else {
            LOGGER.info("Method 'loadUserByUsername' get user.");
            return userFromDB;
        }
    }

    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public boolean addUser(User user) {
        LOGGER.info("Method 'addUser' is started.");
        User userFromDB = userRepo.findByUsername(user.getUsername());
        User userFromDB1 = userRepo.findByEmail(user.getEmail());
        if (userFromDB != null || userFromDB1 != null) {
            LOGGER.debug("User with such name or email exists");
            return true;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(Role.USER));
        user.setCode(UUID.randomUUID().toString());
        userRepo.save(user);
        LOGGER.info(user.getUsername() + " was added to Db");
        sendMessage(user, "Щоб підтвердити реєстрацію на сервісі", "activate/%s");
        return false;
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    public void update(Long id, User user, Map<String, String> form) {
        LOGGER.info("Method 'update' is started.");
        User userToBeUpdated = userRepo.findById(id).get();
        userToBeUpdated.setUsername(user.getUsername());
        userToBeUpdated.setPassword(passwordEncoder.encode(user.getPassword()));
        userToBeUpdated.getRoles().clear();
        for (Role role : Role.values()) {
            if (form.containsValue(role.toString())) {
                userToBeUpdated.getRoles().add(role);
            }
        }
        userRepo.save(userToBeUpdated);
    }

    private void sendMessage(User user, String text, String link) {
        LOGGER.info("Method 'sendMessage' is started.");
        if (!StringUtils.isEmpty(user.getEmail())) {
            String message = String.format("Привіт, %s \n" + text + ", будь ласка перейди за наступним посиланням: " +
                    "http://localhost:8080/" + link, user.getUsername(), user.getCode());
            mailSender.send(user.getEmail(), "Код активації", message);
        }
    }

    public boolean activateUser(String activationCode) {
        LOGGER.info("Method 'activateUser' is started.");
        User user = userRepo.findByCode(activationCode);
        if (user == null) {
            LOGGER.debug("Activation code not found");
            return false;
        }
        user.setActive(true);
        user.setCode(null);
        userRepo.save(user);
        LOGGER.info("User is activated");
        return true;
    }

    public void updateBestResult(User user, int count){
        LOGGER.info("Method 'updateBestResult' is started");
        if(count> user.getBestResult()){
         user.setBestResult(count);
          userRepo.save(user);
            LOGGER.info("User "+user.getUsername()+" has the new best result: "+count);
        }
    }

    public List<CurrentGame> getGames() {
        return games;
    }

    public void initializeUser() {
        User firstUser = new User();
        firstUser.setUsername("first");
        firstUser.setEmail("first@gmail.com");
        firstUser.setPassword(passwordEncoder.encode("111111"));
        firstUser.setRoles(Collections.singleton(Role.USER));
        firstUser.setRoles(Collections.singleton(Role.ADMIN));
        firstUser.setActive(true);
        userRepo.save(firstUser);
    }
}
