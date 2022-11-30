package com.example.StepByStep.services;

import com.example.StepByStep.entities.Role;
import com.example.StepByStep.entities.User;
import com.example.StepByStep.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SmtpMailSender mailSender;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userFromDB = userRepo.findByUsername(username);
        if (userFromDB == null) {
            throw new UsernameNotFoundException("Користувач не знайдений");
        } else if (userFromDB.getCode()!=null) {
            throw new UsernameNotFoundException("Користувач не активований");
        } else {
            return userFromDB;
        }
    }

    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public boolean addUser(User user) {
        User userFromDB = userRepo.findByUsername(user.getUsername());
        User userFromDB1 = userRepo.findByEmail(user.getEmail());
        if(userFromDB!=null||userFromDB1!=null){
            return true;
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(Role.USER));
        user.setCode(UUID.randomUUID().toString());
        userRepo.save(user);

        sendMessage(user, "Щоб підтвердити реєстрацію на сервісі", "activate/%s" );
        return false;
    }

    public List<User> findAll() {
    return userRepo.findAll();
    }

    public void update(Long id, User user, Map<String, String> form) {
        User userToBeUpdated = userRepo.findById(id).get();
        userToBeUpdated.setUsername(user.getUsername());
        userToBeUpdated.setPassword(passwordEncoder.encode(user.getPassword()));
        userToBeUpdated.getRoles().clear();
        for(Role role:Role.values()){
            if(form.containsValue(role.toString())){
                userToBeUpdated.getRoles().add(role);
            }
        }
        userRepo.save(userToBeUpdated);
    }

    private void sendMessage(User user, String text, String link) {
        if (!StringUtils.isEmpty(user.getEmail())) {
            String message = String.format("Привіт, %s \n" + text + ", будь ласка перейди за наступним посиланням: " +
                    "http://localhost:8080/" + link, user.getUsername(), user.getCode());
            mailSender.send(user.getEmail(), "Код активації", message);
        }
    }

    public boolean activateUser(String activationCode){
        User user = userRepo.findByCode(activationCode);
        if(user==null){
            return false;
        }
        user.setActive(true);
        user.setCode(null);
        userRepo.save(user);
        return true;
    }

}
