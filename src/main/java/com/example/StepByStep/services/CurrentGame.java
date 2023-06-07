package com.example.StepByStep.services;

import com.example.StepByStep.entities.City;
import com.example.StepByStep.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CurrentGame {
    @Autowired
    private UserService userService;
    private User[] players = new User[2];
    private User player;

    private final List<City> usedCities = new ArrayList<>();

    public User[] getPlayers() {
        return players;
    }

    public void setPlayers(User[] players) {
        this.players = players;
    }

    public List<City> getUsedCities() {
        return usedCities;
    }

    public User getPlayer() {
        return player;
    }

    public void setPlayer(User player) {
        this.player = player;
    }
}