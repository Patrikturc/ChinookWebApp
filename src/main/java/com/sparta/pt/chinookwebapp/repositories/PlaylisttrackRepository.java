package com.sparta.pt.chinookwebapp.repositories;

import com.sparta.pt.chinookwebapp.models.Playlisttrack;
import com.sparta.pt.chinookwebapp.models.PlaylisttrackId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaylisttrackRepository extends JpaRepository<Playlisttrack, PlaylisttrackId> {
    Page<Playlisttrack> findByPlaylistId(Integer id, Pageable pageable);
}