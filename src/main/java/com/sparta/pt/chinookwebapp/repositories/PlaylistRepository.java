package com.sparta.pt.chinookwebapp.repositories;

import com.sparta.pt.chinookwebapp.models.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<Playlist, Integer> {
    Optional<Playlist> findByName(String playlistName);
}