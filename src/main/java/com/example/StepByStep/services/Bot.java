package com.example.StepByStep.services;

import com.example.StepByStep.entities.City;
import com.example.StepByStep.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

@Service
public class Bot {

    @Autowired
    private CityService cityService;

    @Autowired
    private UserService userService;
    private static final Logger LOGGER = LogManager.getLogger(Bot.class);

    private final List<City> knownCities = new ArrayList<>();

    public List<City> getKnownCities() {
        return knownCities;
    }

    public City nameCity(List<City> usedCities) {
        LOGGER.info("Method 'nameCity' in class Bot is started");
        City lastCity = usedCities.get(usedCities.size() - 1);
        makeCityKnown(lastCity);
        String lastLetter = getLastLetter(lastCity.getName());
        return chooseCityFromList(lastLetter, usedCities);
    }

    public boolean isCityUsed(City chosenCity, List<City> usedCities) {
        LOGGER.info("Checking if " + chosenCity.getName() + " is used");
        return usedCities.stream().anyMatch(c -> c.getName().equals(chosenCity.getName()));
    }

    private City chooseCityFromList(String lastLetter, List<City> usedCities) {
        LOGGER.info("Method 'chooseCityFromList' is started");
        City city = knownCities.stream().filter(c -> c.getName().startsWith(lastLetter)).findAny().orElse(null);
        if (city != null) {
            knownCities.removeIf(c -> c.getName().equals(city.getName()));
            if (isCityUsed(city, usedCities)) {
                LOGGER.info(city.getName() + " is used");
                return chooseCityFromList(lastLetter, usedCities);
            } else {
                LOGGER.info(city.getName() + " is not used");
                usedCities.add(city);
                LOGGER.info("Method 'chooseCityFromList' return city called "+city.getName());
                return city;
            }
        } else {
            LOGGER.debug("Bot does not know a city");
            return null;
        }
    }

    private void makeCityKnown(City lastCity) {
        LOGGER.info("Method 'makeCityKnown' is started");
        if (knownCities.stream().noneMatch(city -> city.getName().equals(lastCity.getName()))) {
            LOGGER.info("Adding " + lastCity.getName() + " to DB");
            cityService.addCity(lastCity);
        }
    }

    public String getLastLetter(String name) {
        LOGGER.info("Method 'getLastLetter' in class Bot is started to get last letter from "+name);
        String letter = name.substring(name.length() - 1).toUpperCase();
        if (letter.equals("Ь") || letter.equals("И")) {
            LOGGER.debug("Letter is "+letter+", definition another letter");
            letter = name.substring(name.length() - 2, name.length() - 1).toUpperCase();
        }
        LOGGER.info("Method 'getLastLetter' return "+letter);
        return letter;
    }

    public boolean cityStartsWith(City city, List<City> usedCities) {
        LOGGER.info("Method 'isCityMatched' in class Bot is started");
        int size = usedCities.size();
        String lastLetter = getLastLetter(usedCities.get(size - 1).getName());
        return city.getName().startsWith(lastLetter);
    }

    public void removeGame(User user, CurrentGame game, Model model) {
        LOGGER.info("Method 'removeGame' is started. Current user - "+user.getUsername());
        model.addAttribute("count", "Ти використав " + user.getCount() + " міст(а)");
        userService.updateBestResult(user, user.getCount());
        user.setCount(0);
        userService.getGames().remove(game);
    }
}
