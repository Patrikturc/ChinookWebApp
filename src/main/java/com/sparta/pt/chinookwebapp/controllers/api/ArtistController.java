package com.sparta.pt.chinookwebapp.controllers.api;

import com.sparta.pt.chinookwebapp.dtos.ArtistDTO;
import com.sparta.pt.chinookwebapp.models.Artist;
import com.sparta.pt.chinookwebapp.services.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/artists")
public class ArtistController {

    private final ArtistService artistService;

    @Autowired
    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    public List<Artist> getAllArtists() {
        return artistService.getAllArtists();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Artist> getArtistById(@PathVariable Integer id) {
        Optional<Artist> artist = artistService.getArtistById(id);
        return artist.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{artistName}")
    public ResponseEntity<List<EntityModel<ArtistDTO>>> getArtistByName(@PathVariable String artistName) {
        List<Artist> artists = artistService.getArtistByName(artistName);
        if (!artists.isEmpty()) {
            List<EntityModel<ArtistDTO>> artistDTOs = artists.stream()
                    .map(artist -> new ArtistDTO(artist.getId(), artist.getName()))
                    .map(EntityModel::of)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(artistDTOs);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public Artist createArtist(@RequestBody Artist artist) {
        return artistService.createArtist(artist);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Artist> updateArtist(@PathVariable Integer id, @RequestBody Artist artistDetails) {
        Optional<Artist> updatedArtist = Optional.ofNullable(artistService.upsertArtist(id, artistDetails));
        return updatedArtist.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Artist> patchArtist(@PathVariable Integer id, @RequestBody Artist artistDetails) {
        Optional<Artist> updatedArtist = artistService.patchArtist(id, artistDetails);
        return updatedArtist
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable Integer id) {
        boolean isDeleted = artistService.deleteArtist(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}