package com.example.StepByStep.controllers;

import com.example.StepByStep.entities.User;
import com.example.StepByStep.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class RegistrationController {
    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    public String registration(User user) {
        return "user/registration";
    }

    @PostMapping("/registration")
    public String addUser(@Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "user/registration";
        }
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            model.addAttribute("passwordError", "Різні паролі");
            return "user/registration";
        }
        boolean exists = userService.addUser(user);
        if (exists) {
            model.addAttribute("message", "Користувач з таким іменем або поштою вже існує");
            return "user/registration";
        } else {
            model.addAttribute("message", "Успішна реєстрація, щоб активувати " +
                    "ваш акаунт перейдіть за посиланням" + ", яке відправлене на вашу пошту");
            return "user/login";
        }
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code) {
        boolean isActivated = userService.activateUser(code);
        if (isActivated) {
            model.addAttribute("message", "Користувач успішно активований");
        } else {
            model.addAttribute("message", "Код активації не знайдено");
        }
        return "user/login";
    }

}
