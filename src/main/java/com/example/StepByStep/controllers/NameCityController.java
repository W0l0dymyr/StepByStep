package com.example.StepByStep.controllers;

import com.example.StepByStep.entities.City;
import com.example.StepByStep.entities.User;
import com.example.StepByStep.services.Bot;
import com.example.StepByStep.services.CityService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequestMapping("/game/name_city")
public class NameCityController {
    private static final Logger LOGGER = LogManager.getLogger(NameCityController.class);

    @Autowired
    private Bot bot;

    @Autowired
    private CityService cityService;

    @GetMapping
    public String nameCityMenu() {
        LOGGER.info("Getting view game/name_city/menu");
        return "game/name_city/menu";
    }

    @GetMapping("/play")
    public String enterCity(City city, @AuthenticationPrincipal User user, Model model) {
        LOGGER.info("Method 'enterCity' is started by '/play'");
        if (user.getUsedCities().size() == 0) {
            bot.getKnownCities().addAll(cityService.findAll());
            return "game/name_city/enter_city";
        }
        City botsCity = bot.nameCity(user);
        if (botsCity == null) {
            LOGGER.info("You win");
            user.getUsedCities().clear();
            model.addAttribute("count", "Ти використав " + user.getCount() + " населених пунктів");
            user.setCount(0);
            return "game/name_city/you_win";
        }
        model.addAttribute("botsCity", botsCity.getName());
        return "game/name_city/enter_city";
    }

    @PostMapping("/save_used")
    public String saveCity(@AuthenticationPrincipal User user
            , @Valid City city, BindingResult bindingResult, Model model) {
        LOGGER.info("Method 'saveCity is started'");
        if (bindingResult.hasErrors()) {
            LOGGER.info("City name can not be empty");
            return "game/name_city/enter_city";
        }
        if (user.getUsedCities().size() > 0) {
            LOGGER.info("user.getUsedCities().size()>0");
            boolean isCityMatched = bot.isCityMatched(city, user.getUsedCities());
            boolean isCityUsed = bot.isCityUsed(city, user.getUsedCities());
            if (isCityUsed || !isCityMatched) {
                LOGGER.info(city.getName() + " is used or not matched");
                model.addAttribute("botsCity", user.getUsedCities().get(user.getUsedCities().size() - 1).getName());
                model.addAttribute("message", "Місто починається з іншої літери" +
                        " або таке місто вже використане");
                return "/game/name_city/enter_city";
            }
        }
        user.getUsedCities().add(city);
        user.setCount(user.getCount() + 1);
        return "redirect:/game/name_city/play";
    }

    @GetMapping("/game_over")
    public String gameOver(@AuthenticationPrincipal User user, Model model) {
        LOGGER.info("You lose");
        user.getUsedCities().clear();
        model.addAttribute("count", "Ти використав " + user.getCount() + " населених пунктів");
        user.setCount(0);
        return "game/name_city/game_over";
    }
}
