package com.example.StepByStep.controllers;

import com.example.StepByStep.entities.City;
import com.example.StepByStep.services.CurrentGame;
import com.example.StepByStep.entities.User;
import com.example.StepByStep.services.Bot;
import com.example.StepByStep.services.CityService;
import com.example.StepByStep.services.UserService;
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

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.lang.Thread.onSpinWait;

@Controller
@RequestMapping("/game/name_city")
public class NameCityController {
    private static final Logger LOGGER = LogManager.getLogger(NameCityController.class);

    @Autowired
    private Bot bot;

    @Autowired
    private CityService cityService;

    @Autowired
    private UserService userService;


    @GetMapping
    public String nameCityMenu() {
        LOGGER.info("Getting view game/name_city/menu");
        return "game/name_city/menu";
    }

    @GetMapping("/play")
    public String enterCity(City city, @AuthenticationPrincipal User user, Model model) {
        LOGGER.info("Method 'enterCity' is started. Current user - "+user.getUsername());
        if (user.getCount() == 0) {
            LOGGER.info("user.getCount() == 0");
            CurrentGame game = new CurrentGame();
            game.setPlayer(user);
            userService.getGames().add(game);
            bot.getKnownCities().addAll(cityService.findAll());
            return "game/name_city/enter_city";
        }
        CurrentGame game = userService.getGames().stream().filter(g -> g.getPlayer().equals(user)).findAny().orElse(new CurrentGame());
        City botsCity = bot.nameCity(game.getUsedCities());
        if (botsCity == null) {
             bot.removeGame(user, game, model);
             return "game/name_city/you_win";
        }
        model.addAttribute("lastCity", botsCity.getName());
        return "game/name_city/enter_city";
    }


    @PostMapping("/save_used")
    public String saveCity(@AuthenticationPrincipal User user, @Valid City city, BindingResult bindingResult, Model model) {
        LOGGER.info("Method 'saveCity is started'. Current user - "+user.getUsername());
        if (bindingResult.hasErrors()) {
            LOGGER.debug("City name can not be empty");
            return "game/name_city/enter_city";
        }
        Supplier<CurrentGame> supplier = () -> userService.getGames().stream().filter(g -> g.getPlayer().equals(user)).findAny().orElse(new CurrentGame());
        CurrentGame game = userService.getGames().stream().filter(g -> Arrays.asList(g.getPlayers()).contains(user)).findAny().orElseGet(supplier);
        if (user.equals(game.getPlayers()[0]) && game.getUsedCities().size() % 2 == 1
                || user.equals(game.getPlayers()[1]) && game.getUsedCities().size() % 2 == 0) {
            LOGGER.debug("User "+ user.getUsername()+ " was trying to enter city out of sequence ");
            return "redirect:/game/name_city/play_with";
        }
        if (game.getUsedCities().size()>0&&(bot.isCityUsed(city, game.getUsedCities()) || !bot.cityStartsWith(city, game.getUsedCities()))) {
                model.addAttribute("message", "Місто починається з іншої літери" +
                        " або таке місто вже використане");
                model.addAttribute("lastCity", game.getUsedCities().get(game.getUsedCities().size() - 1).getName());
                return "/game/name_city/enter_city";
            }else {
                user.setCount(user.getCount() + 1);
                game.getUsedCities().add(city);
                cityService.addCity(city);
                if (Arrays.asList(game.getPlayers()).contains(user)) {
                    return "redirect:/game/name_city/play_with";
                } else {
                    return "redirect:/game/name_city/play";
                }
            }
    }

    @GetMapping("/play_with")
    public String playWith(City city, @AuthenticationPrincipal User user, Model model) throws InterruptedException {
        LOGGER.info("Method 'playWith' is started. Current user - "+user.getUsername());
        CurrentGame game = userService.getGames().stream().filter(r -> Arrays.asList(r.getPlayers()).contains(user)).findAny()
                .orElse(userService.getGames().stream().filter(r -> r.getPlayers()[1] == null).findAny().orElse(new CurrentGame()));
        if (userService.getGames().isEmpty() || user.getCount() == 0) {
            if (userService.getGames().isEmpty() || userService.getGames().stream().noneMatch(r -> r.getPlayers()[1] == null)) {
                LOGGER.info("games.isEmpty(). Creating a new game by "+user.getUsername());
                game.getPlayers()[0] = user;
                userService.getGames().add(game);
                return "game/name_city/enter_city";
            } else {
                LOGGER.info("Joining "+user.getUsername()+" to game");
                CurrentGame room = userService.getGames().stream().filter(r -> r.getPlayers()[1] == null).findAny().get();
                room.getPlayers()[1] = user;
            }
        }
        if (game.getPlayers()[0].equals(user)) {
            while ((game.getPlayers()[1] != null || game.getUsedCities().size() == 1) && game.getUsedCities().size() % 2 == 1) {
                onSpinWait();
                LOGGER.info("Method 'playWith' was waiting. Current user - "+user.getUsername());
            }
            if (game.getPlayers()[1] == null) {
                 bot.removeGame(user, game, model);
                return "game/name_city/you_win";
            }
        } else {
            while (game.getUsedCities().size() % 2 == 0 && game.getPlayers()[0] != null) {
                onSpinWait();
                LOGGER.info("Method 'playWith' was waiting. Current user - "+user.getUsername());
            }
            if (game.getPlayers()[0] == null) {
                bot.removeGame(user, game, model);
                return "game/name_city/you_win";
            }
        }
        int size = game.getUsedCities().size();
        model.addAttribute("lastCity", game.getUsedCities().get(size - 1).getName());
        return "game/name_city/enter_city";
    }

    @GetMapping("/game_over")
    public String gameOver(@AuthenticationPrincipal User user, Model model) {
        LOGGER.info("Method 'gameOver' is started. Current user - "+user.getUsername());
        CurrentGame game = userService.getGames().stream().filter(r -> Arrays.asList(r.getPlayers()).contains(user)).findAny()
                .orElse(userService.getGames().stream().filter(r -> r.getPlayers()[1] == null).findAny().orElse(new CurrentGame()));
        if (game.getPlayers()[0] != null) {
            if (game.getPlayers()[0] == user) {
                game.getPlayers()[0] = null;
            } else {
                game.getPlayers()[1] = null;
            }
        }
        bot.removeGame(user, game, model);
        return "game/name_city/game_over";
    }

    @GetMapping("/best_results")
    public String allBestResults(Model model){
        LOGGER.info("Method 'allBestResults' is started.");
        model.addAttribute("users", userService.findAll().stream()
                .sorted(Collections.reverseOrder(Comparator.comparing(User::getBestResult).thenComparing(User::getUsername)))
                .collect(Collectors.toList()));
        return "game/name_city/best_results";
    }

}
