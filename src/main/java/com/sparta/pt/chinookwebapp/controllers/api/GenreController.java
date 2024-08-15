package com.sparta.pt.chinookwebapp.controllers.api;

import com.sparta.pt.chinookwebapp.dtos.GenreDTO;
import com.sparta.pt.chinookwebapp.services.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<GenreDTO> getAllGenres() {
        return genreService.getAllGenres();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreDTO> getGenreById(@PathVariable Integer id) {
        Optional<GenreDTO> genre = genreService.getGenreById(id);
        return genre.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public GenreDTO createGenre(@RequestBody GenreDTO genreDTO) {
        return genreService.createGenre(genreDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenreDTO> updateGenre(@PathVariable Integer id, @RequestBody GenreDTO genreDTO) {
        Optional<GenreDTO> updatedGenre = Optional.ofNullable(genreService.upsertGenre(id, genreDTO));
        return updatedGenre.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<GenreDTO> patchGenre(@PathVariable Integer id, @RequestBody GenreDTO genreDTO) {
        Optional<GenreDTO> patchedGenreDTO = genreService.patchGenre(id, genreDTO);

        return patchedGenreDTO
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Integer id) {
        boolean isDeleted = genreService.deleteGenre(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}