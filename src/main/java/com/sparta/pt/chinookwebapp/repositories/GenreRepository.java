package com.sparta.pt.chinookwebapp.repositories;

import com.sparta.pt.chinookwebapp.models.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Integer> {
    Optional<Genre> getGenreByName(String genreName);
}