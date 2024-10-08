package com.sparta.pt.chinookwebapp.controllers.web;

import com.sparta.pt.chinookwebapp.dtos.TrackDTO;
import com.sparta.pt.chinookwebapp.controllers.api.TrackController;
import com.sparta.pt.chinookwebapp.utils.CustomPagedResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.Objects;

@Controller
public class HomeController {

    private final TrackController trackController;

    public HomeController(TrackController trackController) {
        this.trackController = trackController;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/view-tracks")
    public String viewTracks(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        ResponseEntity<CustomPagedResponse<TrackDTO>> response = trackController.getAllTracks(page, size);

        CustomPagedResponse<TrackDTO> tracks = response.getBody();
        assert tracks != null;

        model.addAttribute("tracks", tracks.content());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", tracks.page().totalPages());

        return "view-tracks";
    }

    @GetMapping("/add-track")
    public String showAddTrackForm(Model model, HttpServletRequest request) {
        ResponseEntity<CustomPagedResponse<TrackDTO>> response = trackController.getAllTracks(0, Integer.MAX_VALUE);
        if (response.getStatusCode().is2xxSuccessful()) {
            CustomPagedResponse<TrackDTO> tracks = response.getBody();

            if (tracks != null && !tracks.content().isEmpty()) {
                TrackDTO lastTrack = tracks.content().stream()
                        .map(EntityModel::getContent)
                        .filter(Objects::nonNull) // Make sure to handle potential nulls in content
                        .max(Comparator.comparingInt(TrackDTO::getId))
                        .orElse(new TrackDTO());

                model.addAttribute("track", lastTrack);
            } else {
                model.addAttribute("track", new TrackDTO());
            }
        } else {
            model.addAttribute("track", new TrackDTO());
        }
        model.addAttribute("referrer", request.getHeader("Referer"));
        return "add-track";
    }

    @GetMapping("/update-track/{id}")
    public String showUpdateTrackForm(@PathVariable int id, Model model, HttpServletRequest request) {
        ResponseEntity<TrackDTO> response = trackController.getTrackById(id);
        if (response.getStatusCode().is2xxSuccessful()) {
            TrackDTO track = response.getBody();
            model.addAttribute("track", track);
        } else {
            model.addAttribute("error", "Track not found");
        }
        model.addAttribute("referrer", request.getHeader("Referer"));
        return "update-track";
    }

    @PostMapping("/add-track")
    public String addTrack(@ModelAttribute TrackDTO trackDTO) {
        trackController.createTrack(trackDTO);
        return "redirect:/view-tracks";
    }

    @PostMapping("/update-track/{id}")
    public String updateTrack(@PathVariable int id, @ModelAttribute TrackDTO trackDTO) {
        trackController.updateTrack(id, trackDTO);
        return "redirect:/view-tracks";
    }

    @DeleteMapping("/delete-track/{id}")
    public ResponseEntity<String> deleteTrack(@PathVariable int id) {
        ResponseEntity<TrackDTO> response = trackController.getTrackById(id);
        if (response.getStatusCode().is2xxSuccessful()) {
            trackController.deleteTrack(id);
            return ResponseEntity.ok("Track deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }
}