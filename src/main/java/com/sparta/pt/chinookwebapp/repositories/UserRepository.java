package com.sparta.pt.chinookwebapp.repositories;

import com.sparta.pt.chinookwebapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}