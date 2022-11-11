package com.example.StepByStep.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

@GetMapping("/")
    public String main(){
    return "main";
}

@GetMapping("/menu")
    public String menu(){
    return "menu";
}
}
