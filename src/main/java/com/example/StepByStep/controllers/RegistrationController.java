package com.example.StepByStep.controllers;

import com.example.StepByStep.entities.User;
import com.example.StepByStep.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {
@Autowired
private UserService userService;
    @GetMapping("/registration")
    public String registration(){
        return "user/registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Model model){
        User userFromDB = userService.findByUsername(user.getUsername());
    if(userFromDB!=null){
        model.addAttribute("message", "Користувач з таким іменем вже існує");
        return "user/registration";
    }else{
        userService.addUser(user);
        model.addAttribute("message", "Успішна реєстрація");
        return "user/login";
    }
    }
}
