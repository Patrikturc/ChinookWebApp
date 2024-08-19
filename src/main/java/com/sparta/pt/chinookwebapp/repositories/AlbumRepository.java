package com.sparta.pt.chinookwebapp.repositories;

import com.sparta.pt.chinookwebapp.models.Album;
import com.sparta.pt.chinookwebapp.models.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface AlbumRepository extends JpaRepository<Album, Integer> {
    Page<Album> findByArtistIn(List<Artist> artists, Pageable pageable);
}