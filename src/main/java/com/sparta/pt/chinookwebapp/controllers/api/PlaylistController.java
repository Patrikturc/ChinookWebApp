package com.sparta.pt.chinookwebapp.controllers.api;

import com.sparta.pt.chinookwebapp.dtos.PlaylistDTO;
import com.sparta.pt.chinookwebapp.services.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;

    @GetMapping
    public ResponseEntity<Page<PlaylistDTO>> getAllPlaylists(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "100") int size) {
        Page<PlaylistDTO> playlists = playlistService.getAllPlaylists(page, size);
        return ResponseEntity.ok(playlists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDTO> getPlaylistById(@PathVariable int id) {
        Optional<PlaylistDTO> playlist = playlistService.getPlaylistById(id);
        return playlist.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PlaylistDTO> createPlaylist(@RequestBody PlaylistDTO playlistDTO) {
        PlaylistDTO createdPlaylist = playlistService.createPlaylist(playlistDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPlaylist);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlaylistDTO> upsertPlaylist(@PathVariable int id, @RequestBody PlaylistDTO playlistDTO) {
        PlaylistDTO upsertedPlaylist = playlistService.upsertPlaylist(id, playlistDTO);
        return ResponseEntity.ok(upsertedPlaylist);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PlaylistDTO> updatePlaylist(@PathVariable int id, @RequestBody PlaylistDTO playlistDTO) {
        Optional<PlaylistDTO> updatedPlaylist = playlistService.updatePlaylist(id, playlistDTO);
        return updatedPlaylist.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable int id) {
        if (playlistService.deletePlaylist(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}