package com.sparta.pt.chinookwebapp.controllers.api;

import com.sparta.pt.chinookwebapp.dtos.ArtistDTO;
import com.sparta.pt.chinookwebapp.services.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/artists")
public class ArtistController {

    private final ArtistService artistService;

    @Autowired
    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<ArtistDTO>>> getAllArtists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        return artistService.getAllArtists(page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<ArtistDTO>> getArtistById(@PathVariable Integer id) {
        return artistService.getArtistById(id);
    }

    @GetMapping("/name/{artistName}")
    public ResponseEntity<PagedModel<EntityModel<ArtistDTO>>> getArtistByName(
            @PathVariable String artistName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        return artistService.getArtistByName(artistName, page, size);
    }

    @PostMapping
    public ResponseEntity<EntityModel<ArtistDTO>> createArtist(@RequestBody ArtistDTO artistDTO) {
        return artistService.createArtist(artistDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<ArtistDTO>> updateArtist(@PathVariable Integer id, @RequestBody ArtistDTO artistDTO) {
        return artistService.updateArtist(id, artistDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EntityModel<ArtistDTO>> patchArtist(@PathVariable Integer id, @RequestBody ArtistDTO artistDTO) {
        return artistService.patchArtist(id, artistDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable Integer id) {
        return artistService.deleteArtist(id);
    }

    private String urlDecode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}