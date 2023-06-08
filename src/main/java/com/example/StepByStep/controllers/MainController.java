package com.example.StepByStep.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class MainController {
    private static final Logger LOGGER = LogManager.getLogger(MainController.class);

    @GetMapping("/")
    public String menu() {
        LOGGER.info("Method 'menu' is started.");
        return "menu";
    }

    @GetMapping("/game")
    public String chooseGame() {
        LOGGER.info("Method 'chooseGame' is started.");
        return "game/choose_game";
    }
    @ModelAttribute("remoteUser")
    public Object remoteUser(final HttpServletRequest request) {
        return request.getRemoteUser();
    }
}
