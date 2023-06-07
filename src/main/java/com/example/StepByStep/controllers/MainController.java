package com.example.StepByStep.controllers;

import com.example.StepByStep.services.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    private static final Logger LOGGER = LogManager.getLogger(MainController.class);

    @GetMapping("/")
    public String main() {
        LOGGER.info("Method 'main' is started.");
        return "main";
    }

    @GetMapping("/menu")
    public String menu() {
        LOGGER.info("Method 'menu' is started.");
        return "menu";
    }

    @GetMapping("/game")
    public String chooseGame() {
        LOGGER.info("Method 'chooseGame' is started.");
        return "game/choose_game";
    }
}
