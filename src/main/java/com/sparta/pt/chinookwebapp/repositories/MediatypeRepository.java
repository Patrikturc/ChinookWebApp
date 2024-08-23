package com.sparta.pt.chinookwebapp.repositories;

import com.sparta.pt.chinookwebapp.models.Mediatype;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MediatypeRepository extends JpaRepository<Mediatype, Integer> {
    Optional<Mediatype> getMediatypeByName(String mediaTypeName);
}