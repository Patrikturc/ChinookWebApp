package com.sparta.pt.chinookwebapp.controllers.api;

import com.sparta.pt.chinookwebapp.assemblers.PlaylistTrackDTOAssembler;
import com.sparta.pt.chinookwebapp.dtos.PlaylistTrackDTO;
import com.sparta.pt.chinookwebapp.services.PlaylistTrackService;
import com.sparta.pt.chinookwebapp.utils.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/playlisttracks")
public class PlaylistTrackController {

    private final PlaylistTrackService playlistTrackService;
    private final PlaylistTrackDTOAssembler assembler;
    private final PaginationUtils<PlaylistTrackDTO> paginationUtils;

    public PlaylistTrackController(PlaylistTrackService playlistTrackService,
                                   PlaylistTrackDTOAssembler assembler,
                                   PaginationUtils<PlaylistTrackDTO> paginationUtils) {
        this.playlistTrackService = playlistTrackService;
        this.assembler = assembler;
        this.paginationUtils = paginationUtils;
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<PlaylistTrackDTO>>> getAllPlaylistTracks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {

        Page<PlaylistTrackDTO> playlistTracks = playlistTrackService.getAllPlaylistTracks(page, size);
        PagedModel<EntityModel<PlaylistTrackDTO>> pagedResources = paginationUtils.createPagedResponse(
                playlistTracks.map(assembler::toModel), PlaylistTrackController.class, PlaylistTrackDTO::getPlaylistId).getBody();

        return ResponseEntity.ok(pagedResources);
    }

    @GetMapping("/{playlistName}")
    public ResponseEntity<PagedModel<EntityModel<PlaylistTrackDTO>>> getTracksByPlaylistName(
            @PathVariable String playlistName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {

        Page<PlaylistTrackDTO> tracks = playlistTrackService.getTracksByPlaylistName(playlistName, page, size);
        PagedModel<EntityModel<PlaylistTrackDTO>> pagedResources = paginationUtils.createPagedResponse(
                tracks.map(assembler::toModel), PlaylistTrackController.class, PlaylistTrackDTO::getPlaylistId).getBody();

        return ResponseEntity.ok(pagedResources);
    }

    @GetMapping("/{playlistName}/{trackName}")
    public ResponseEntity<PlaylistTrackDTO> getPlaylistTrackById(
            @PathVariable String playlistName,
            @PathVariable String trackName) {

        Optional<PlaylistTrackDTO> playlistTrack = playlistTrackService.getPlaylistTrackById(playlistName, trackName);
        return playlistTrack.map(dto -> ResponseEntity.ok(assembler.toModel(dto)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PlaylistTrackDTO> createPlaylistTrack(@RequestBody PlaylistTrackDTO playlistTrackDTO) {
        PlaylistTrackDTO createdPlaylistTrack = playlistTrackService.createPlaylistTrack(playlistTrackDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(createdPlaylistTrack));
    }

    @PutMapping("/{playlistName}/{trackName}")
    public ResponseEntity<PlaylistTrackDTO> upsertPlaylistTrack(
            @PathVariable String playlistName,
            @PathVariable String trackName,
            @RequestBody PlaylistTrackDTO playlistTrackDTO) {

        PlaylistTrackDTO upsertedPlaylistTrack = playlistTrackService.upsertPlaylistTrack(playlistName, trackName, playlistTrackDTO);
        return ResponseEntity.ok(assembler.toModel(upsertedPlaylistTrack));
    }

    @PatchMapping("/{playlistName}/{trackName}")
    public ResponseEntity<PlaylistTrackDTO> updatePlaylistTrack(
            @PathVariable String playlistName,
            @PathVariable String trackName,
            @RequestBody PlaylistTrackDTO playlistTrackDTO) {

        Optional<PlaylistTrackDTO> updatedPlaylistTrack = playlistTrackService.updatePlaylistTrack(playlistName, trackName, playlistTrackDTO);
        return updatedPlaylistTrack.map(dto -> ResponseEntity.ok(assembler.toModel(dto)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{playlistName}/{trackName}")
    public ResponseEntity<Void> deletePlaylistTrack(@PathVariable String playlistName,
                                                    @PathVariable String trackName) {
        if (playlistTrackService.deletePlaylistTrack(playlistName, trackName)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}