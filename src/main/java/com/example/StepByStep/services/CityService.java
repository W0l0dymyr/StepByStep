package com.example.StepByStep.services;

import com.example.StepByStep.entities.City;
import com.example.StepByStep.repositories.CityRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityService {
    @Autowired
    private CityRepo cityRepository;

    public List<City> findAll() {
        return cityRepository.findAll();
    }

    public void addCity(City city) {
        if (cityRepository.findByName(city.getName()) == null) {
            cityRepository.save(city);
        }
    }
}
