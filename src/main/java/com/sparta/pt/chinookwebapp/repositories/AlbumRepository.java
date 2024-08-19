package com.sparta.pt.chinookwebapp.repositories;

import com.sparta.pt.chinookwebapp.models.Album;
import com.sparta.pt.chinookwebapp.models.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface AlbumRepository extends JpaRepository<Album, Integer> {
    Collection<Album> findByArtist(Artist artist);
}