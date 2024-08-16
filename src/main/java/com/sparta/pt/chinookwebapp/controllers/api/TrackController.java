// TrackController.java
package com.sparta.pt.chinookwebapp.controllers.api;

import com.sparta.pt.chinookwebapp.dtos.TrackDTO;
import com.sparta.pt.chinookwebapp.services.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tracks")
public class TrackController {

    @Autowired
    private TrackService trackService;

    @GetMapping
    public List<TrackDTO> getAllTracks() {
        return trackService.getAllTracks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrackDTO> getTrackById(@PathVariable int id) {
        Optional<TrackDTO> trackDTO = trackService.getTrackById(id);
        return trackDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
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