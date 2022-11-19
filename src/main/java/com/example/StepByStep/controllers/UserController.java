package com.example.StepByStep.controllers;

import com.example.StepByStep.entities.Role;
import com.example.StepByStep.entities.User;
import com.example.StepByStep.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public String allUsers(Model model) {
        model.addAttribute("allUsers", userService.findAll());
        return "user/all_users";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("{user}")
    public String editUser(@PathVariable("user") User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "user/edit";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("{id}")
    public String userSave(@PathVariable("id") Long id, @ModelAttribute("user") User user, @RequestParam Map<String, String> form){
        userService.update(id, user, form);
        return "redirect:/user";
    }
}
