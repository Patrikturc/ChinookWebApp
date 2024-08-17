// TrackController.java
package com.sparta.pt.chinookwebapp.controllers.api;

import com.sparta.pt.chinookwebapp.dtos.TrackDTO;
import com.sparta.pt.chinookwebapp.services.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/tracks")
public class TrackController {

    private final TrackService trackService;
    private final PaginationUtils<TrackDTO> paginationUtils;

    @Autowired
    public TrackController(TrackService trackService, PaginationUtils<TrackDTO> paginationUtils) {
        this.trackService = trackService;
        this.paginationUtils = paginationUtils;
    }

    @GetMapping("/albums/{albumTitle}")
    public ResponseEntity<PagedModel<EntityModel<TrackDTO>>> getTracksByAlbumTitle(
            @PathVariable String albumTitle,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {

        Page<TrackDTO> tracksPage = trackService.getTracksByAlbumTitle(albumTitle, page, size);
        return paginationUtils.createPagedResponse(tracksPage, TrackController.class, TrackDTO::getId);
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<TrackDTO>>> getAllTracks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {

        Page<TrackDTO> tracksPage = trackService.getAllTracks(page, size);
        return paginationUtils.createPagedResponse(tracksPage, TrackController.class, TrackDTO::getId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrackDTO> getTrackById(@PathVariable int id) {
        Optional<TrackDTO> trackDTO = trackService.getTrackById(id);
        return trackDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/genres/{genreName}")
    public ResponseEntity<PagedModel<EntityModel<TrackDTO>>> getTracksByGenreName(
            @PathVariable String genreName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {

        Page<TrackDTO> tracksPage = trackService.getTracksByGenreName(genreName, page, size);
        return paginationUtils.createPagedResponse(tracksPage, TrackController.class, TrackDTO::getId);
    }

    @PostMapping
    public TrackDTO createTrack(@RequestBody TrackDTO trackDTO) {
        return trackService.createTrack(trackDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrackDTO> upsertTrack(@PathVariable int id, @RequestBody TrackDTO trackDTO) {
        TrackDTO upsertedTrack = trackService.upsertTrack(id, trackDTO);
        return ResponseEntity.ok(upsertedTrack);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TrackDTO> updateTrack(@PathVariable int id, @RequestBody TrackDTO trackDTO) {
        Optional<TrackDTO> updatedTrack = trackService.updateTrack(id, trackDTO);
        return updatedTrack.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrack(@PathVariable int id) {
        if (trackService.deleteTrack(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}