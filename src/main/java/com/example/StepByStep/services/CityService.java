package com.example.StepByStep.services;

import com.example.StepByStep.entities.City;
import com.example.StepByStep.repositories.CityRepo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {
    @Autowired
    private CityRepo cityRepository;

    private static final Logger LOGGER = LogManager.getLogger(CityService.class);

    public List<City> findAll() {
        return cityRepository.findAll();
    }

    public void addCity(City city) {
        LOGGER.info("Method 'addCity' in class cityService is started");
        if (cityRepository.findByName(city.getName()) == null) {
            cityRepository.save(city);
            LOGGER.info(city.getName()+" is added to Db");
        }
    }
}
