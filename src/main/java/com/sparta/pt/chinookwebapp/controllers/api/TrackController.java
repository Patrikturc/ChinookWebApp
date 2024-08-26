// TrackController.java
package com.sparta.pt.chinookwebapp.controllers.api;

import com.sparta.pt.chinookwebapp.assemblers.TrackDTOAssembler;
import com.sparta.pt.chinookwebapp.dtos.TrackDTO;
import com.sparta.pt.chinookwebapp.services.TrackService;
import com.sparta.pt.chinookwebapp.utils.CustomPagedResponse;
import com.sparta.pt.chinookwebapp.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/tracks")
public class TrackController {

    private final TrackService trackService;
    private final TrackDTOAssembler assembler;
    private final PaginationUtils<TrackDTO> paginationUtils;

    @Autowired
    public TrackController(TrackService trackService, TrackDTOAssembler assembler, PaginationUtils<TrackDTO> paginationUtils) {
        this.trackService = trackService;
        this.assembler = assembler;
        this.paginationUtils = paginationUtils;
    }

    @GetMapping("/albums/{albumTitle}")
    public ResponseEntity<CustomPagedResponse<TrackDTO>> getTracksByAlbumTitle(
            @PathVariable String albumTitle,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {

        Page<TrackDTO> tracksPage = trackService.getTracksByAlbumTitle(albumTitle, page, size);
        return paginationUtils.createPagedResponse(tracksPage.map(assembler::toModel));
    }

    @GetMapping
    public ResponseEntity<CustomPagedResponse<TrackDTO>> getAllTracks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {

        Page<TrackDTO> tracksPage = trackService.getAllTracks(page, size);
        return paginationUtils.createPagedResponse(tracksPage.map(assembler::toModel));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrackDTO> getTrackById(@PathVariable int id) {
        Optional<TrackDTO> trackDTO = trackService.getTrackById(id);
        return trackDTO.map(dto -> ResponseEntity.ok(assembler.toModel(dto)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/genres/{genreName}")
    public ResponseEntity<CustomPagedResponse<TrackDTO>> getTracksByGenreName(
            @PathVariable String genreName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {

        Page<TrackDTO> tracksPage = trackService.getTracksByGenreName(genreName, page, size);
        return paginationUtils.createPagedResponse(tracksPage.map(assembler::toModel));
    }

    @PostMapping
    public ResponseEntity<TrackDTO> createTrack(@RequestBody TrackDTO trackDTO) {
        TrackDTO createdTrack = trackService.createTrack(trackDTO);
        return ResponseEntity.ok(assembler.toModel(createdTrack));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrackDTO> upsertTrack(@PathVariable int id, @RequestBody TrackDTO trackDTO) {
        TrackDTO upsertedTrack = trackService.upsertTrack(id, trackDTO);
        return ResponseEntity.ok(assembler.toModel(upsertedTrack));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TrackDTO> updateTrack(@PathVariable int id, @RequestBody TrackDTO trackDTO) {
        Optional<TrackDTO> updatedTrack = trackService.updateTrack(id, trackDTO);
        return updatedTrack.map(dto -> ResponseEntity.ok(assembler.toModel(dto)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrack(@PathVariable int id) {
        if (trackService.deleteTrack(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}