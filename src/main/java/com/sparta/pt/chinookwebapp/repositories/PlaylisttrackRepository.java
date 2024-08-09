package com.sparta.pt.chinookwebapp.repositories;

import com.sparta.pt.chinookwebapp.models.Playlisttrack;
import com.sparta.pt.chinookwebapp.models.PlaylisttrackId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylisttrackRepository extends JpaRepository<Playlisttrack, PlaylisttrackId> {
}