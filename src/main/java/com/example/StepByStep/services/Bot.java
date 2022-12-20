package com.example.StepByStep.services;

import com.example.StepByStep.entities.City;
import com.example.StepByStep.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Bot {

    @Autowired
    private CityService cityService;
    private static final Logger LOGGER = LogManager.getLogger(Bot.class);

    private final List<City> knownCities = new ArrayList<>();


    public List<City> getKnownCities() {
        return knownCities;
    }

    public City nameCity(User user) {
        LOGGER.info("Method 'nameCity' in class Bot is started");
        City lastCity = user.getUsedCities().get(user.getUsedCities().size() - 1);
        makeCityKnown(lastCity);
        String lastLetter = getLastLetter(lastCity.getName());
        City chosenCity = chooseCityFromList(lastLetter, user.getUsedCities());
        user.getUsedCities().add(chosenCity);
        return chosenCity;
    }

    public boolean isCityUsed(City chosenCity, List<City> usedCities) {
        LOGGER.info("Checking if " + chosenCity.getName() + " is used");
        return usedCities.stream().anyMatch(c -> c.getName().equals(chosenCity.getName()));
    }

    private City chooseCityFromList(String lastLetter, List<City> usedCities) {
        LOGGER.info("Looking for city by name in knownCities");
        City city = knownCities.stream().filter(c -> c.getName().startsWith(lastLetter)).findAny().orElse(null);
        if (city != null) {
            knownCities.removeIf(c -> c.getName().equals(city.getName()));
            if (isCityUsed(city, usedCities)) {
                LOGGER.info(city.getName() + " is used");
                return chooseCityFromList(lastLetter, usedCities);
            } else {
                LOGGER.info(city.getName() + " is not used");
                return city;
            }
        } else {
            LOGGER.info("Bot does not know a city");
            return null;
        }
    }

    private void makeCityKnown(City lastCity) {
        if (knownCities.stream().noneMatch(city -> city.getName().equals(lastCity.getName()))) {
            LOGGER.info("Adding " + lastCity.getName() + " to DB");
            cityService.addCity(lastCity);
        }
    }

    public String getLastLetter(String name) {
        LOGGER.info("Method 'getLastLetter' in class Bot is started");
        String letter = name.substring(name.length() - 1).toUpperCase();
        if (letter.equals("Ь") || letter.equals("И")) {
            LOGGER.debug("Letter is 'ь' or 'и', definition another letter");
            letter = name.substring(name.length() - 2, name.length() - 1).toUpperCase();
        }
        return letter;
    }

    public boolean isCityMatched(City city, List<City> usedCities) {
        LOGGER.info("Method 'isCityMatched' in class Bot is started");
        int size = usedCities.size();
        String lastLetter = getLastLetter(usedCities.get(size - 1).getName());
        return city.getName().startsWith(lastLetter);
    }
}
