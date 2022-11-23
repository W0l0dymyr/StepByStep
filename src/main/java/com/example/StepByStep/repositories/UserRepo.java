package com.example.StepByStep.repositories;

import com.example.StepByStep.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findByCode(String code);
}
