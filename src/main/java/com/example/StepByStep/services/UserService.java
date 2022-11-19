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

import java.util.Collections;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userFromDB = userRepo.findByUsername(username);
        if (userFromDB != null) {
            return userFromDB;
        } else {
            throw new UsernameNotFoundException("Користувач не знайдений");
        }
    }

    public User findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public void addUser(User user) {
        user.setActive(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singleton(Role.USER));
        userRepo.save(user);
    }

    public List<User> findAll() {
    return userRepo.findAll();
    }
}
