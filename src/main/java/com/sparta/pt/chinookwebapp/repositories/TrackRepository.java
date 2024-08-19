package com.sparta.pt.chinookwebapp.repositories;

import com.sparta.pt.chinookwebapp.models.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface TrackRepository extends JpaRepository<Track, Integer> {
    Page<Track> findAll(Pageable pageable);
    Page<Track> findByGenreNameContainingIgnoreCase(String genreName, Pageable pageable);
    Page<Track> findByAlbumTitleContainingIgnoreCase(String albumTitle, Pageable pageable);

    Optional<Track> findByName(String trackName);
}