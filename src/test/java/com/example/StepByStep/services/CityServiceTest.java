package com.example.StepByStep.services;

import com.example.StepByStep.entities.City;
import com.example.StepByStep.repositories.CityRepo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
@RunWith(SpringRunner.class)
@SpringBootTest
public class CityServiceTest {
@Autowired
private CityService cityService;

@MockBean
private CityRepo cityRepo;
    @Test
    public void addCity() {
        City city = new City();
        city.setName("Львів");
        cityService.addCity(city);
        Mockito.verify(cityRepo, Mockito.times(1)).save(city);
        Mockito.doReturn(city).when(cityRepo).findByName("Львів");
        cityService.addCity(city);
        Mockito.verify(cityRepo, Mockito.times(1)).save(city);
    }
}