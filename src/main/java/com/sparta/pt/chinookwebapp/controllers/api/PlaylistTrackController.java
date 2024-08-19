package com.sparta.pt.chinookwebapp.controllers.api;

import com.sparta.pt.chinookwebapp.dtos.PlaylistTrackDTO;
import com.sparta.pt.chinookwebapp.services.PlaylistTrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/playlisttracks")
public class PlaylistTrackController {

    @Autowired
    private PlaylistTrackService playlistTrackService;

    @GetMapping
    public ResponseEntity<Page<PlaylistTrackDTO>> getAllPlaylistTracks(@RequestParam(defaultValue = "0") int page,
                                                                       @RequestParam(defaultValue = "100") int size) {
        Page<PlaylistTrackDTO> playlistTracks = playlistTrackService.getAllPlaylistTracks(page, size);
        return ResponseEntity.ok(playlistTracks);
    }

    @GetMapping("/{playlistName}/{trackName}")
    public ResponseEntity<PlaylistTrackDTO> getPlaylistTrackById(@PathVariable String playlistName, @PathVariable String trackName) {
        Optional<PlaylistTrackDTO> playlistTrack = playlistTrackService.getPlaylistTrackById(playlistName, trackName);
        return playlistTrack.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{playlistName}")
    public ResponseEntity<Page<PlaylistTrackDTO>> getTracksByPlaylistName(@PathVariable String playlistName,
                                                                          @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "100") int size) {
        Page<PlaylistTrackDTO> tracks = playlistTrackService.getTracksByPlaylistName(playlistName, page, size);
        return ResponseEntity.ok(tracks);
    }

    @PostMapping
    public ResponseEntity<PlaylistTrackDTO> createPlaylistTrack(@RequestBody PlaylistTrackDTO playlistTrackDTO) {
        PlaylistTrackDTO createdPlaylistTrack = playlistTrackService.createPlaylistTrack(playlistTrackDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPlaylistTrack);
    }

    @PutMapping("/{playlistName}/{trackName}")
    public ResponseEntity<PlaylistTrackDTO> upsertPlaylistTrack(@PathVariable String playlistName, @PathVariable String trackName, @RequestBody PlaylistTrackDTO playlistTrackDTO) {
        PlaylistTrackDTO upsertedPlaylistTrack = playlistTrackService.upsertPlaylistTrack(playlistName, trackName, playlistTrackDTO);
        return ResponseEntity.ok(upsertedPlaylistTrack);
    }

    @PatchMapping("/{playlistName}/{trackName}")
    public ResponseEntity<PlaylistTrackDTO> updatePlaylistTrack(@PathVariable String playlistName, @PathVariable String trackName, @RequestBody PlaylistTrackDTO playlistTrackDTO) {
        Optional<PlaylistTrackDTO> updatedPlaylistTrack = playlistTrackService.updatePlaylistTrack(playlistName, trackName, playlistTrackDTO);
        return updatedPlaylistTrack.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{playlistName}/{trackName}")
    public ResponseEntity<Void> deletePlaylistTrack(@PathVariable String playlistName, @PathVariable String trackName) {
        if (playlistTrackService.deletePlaylistTrack(playlistName, trackName)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}