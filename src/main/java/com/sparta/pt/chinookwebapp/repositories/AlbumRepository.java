package com.sparta.pt.chinookwebapp.repositories;

import aj.org.objectweb.asm.commons.Remapper;
import com.sparta.pt.chinookwebapp.models.Album;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlbumRepository extends JpaRepository<Album, Integer> {
    Optional<Album> findByTitle(String title);
}