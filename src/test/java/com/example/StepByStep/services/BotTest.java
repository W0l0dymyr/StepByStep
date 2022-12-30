package com.example.StepByStep.services;

import com.example.StepByStep.entities.City;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BotTest {
    @Autowired
    private Bot bot;

    @MockBean
    private UserService userService;
    private City city;
    private List<City> usedCities;

    @Before
    public void prepareData() {
        usedCities = new ArrayList<>();
        city = new City();
        city.setName("Львів");
        usedCities.add(city);
    }

    @Test
    public void nameCity() {
        assertNull(bot.nameCity(usedCities));
        City city1 = new City();
        city1.setName("Варшава");
        bot.getKnownCities().add(city1);
        assertEquals(city1,bot.nameCity(usedCities));
    }

    @Test
    public void isCityUsed() {
        City city = new City();
        city.setName("Варшава");
        Assert.assertFalse(bot.isCityUsed(city, usedCities));
        usedCities.add(city);
        Assert.assertTrue(bot.isCityUsed(city, usedCities));
    }

    @Test
    public void getLastLetter() {
        Assert.assertEquals("В", bot.getLastLetter("Львів"));
        Assert.assertEquals("Л", bot.getLastLetter("Сокаль"));
    }

    @Test
    public void cityStartsWith() {
        City city1 = new City();
        city1.setName("Варшава");
        Assert.assertTrue(bot.cityStartsWith(city1, usedCities));
    }

}