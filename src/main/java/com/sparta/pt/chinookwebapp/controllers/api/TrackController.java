package com.sparta.pt.chinookwebapp.controllers.api;

import com.sparta.pt.chinookwebapp.dtos.TrackDTO;
import com.sparta.pt.chinookwebapp.services.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tracks")
public class TrackController {

    private final TrackService trackService;

    @Autowired
    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<TrackDTO>>> getAllTracks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        return trackService.getAllTracks(page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<TrackDTO>> getTrackById(@PathVariable int id) {
        return trackService.getTrackById(id);
    }

    @GetMapping("/genre/{genreName}")
    public ResponseEntity<PagedModel<EntityModel<TrackDTO>>> getTracksByGenreName(
            @PathVariable String genreName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        return trackService.getTracksByGenreName(genreName, page, size);
    }

    @GetMapping("/album/{albumTitle}")
    public ResponseEntity<PagedModel<EntityModel<TrackDTO>>> getTracksByAlbumTitle(
            @PathVariable String albumTitle,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        return trackService.getTracksByAlbumTitle(albumTitle, page, size);
    }

    @PostMapping
    public ResponseEntity<EntityModel<TrackDTO>> createTrack(@RequestBody TrackDTO trackDTO) {
        return trackService.createTrack(trackDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<TrackDTO>> updateTrack(@PathVariable int id, @RequestBody TrackDTO trackDTO) {
        return trackService.updateTrack(id, trackDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrack(@PathVariable int id) {
        return trackService.deleteTrack(id);
    }
}