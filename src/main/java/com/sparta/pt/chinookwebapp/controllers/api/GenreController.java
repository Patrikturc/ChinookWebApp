package com.sparta.pt.chinookwebapp.controllers.api;

import com.sparta.pt.chinookwebapp.dtos.GenreDTO;
import com.sparta.pt.chinookwebapp.services.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<GenreDTO>>> getAllGenres(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        return genreService.getAllGenres(page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<GenreDTO>> getGenreById(@PathVariable Integer id) {
        return genreService.getGenreById(id);
    }

    @GetMapping("/name/{genreName}")
    public ResponseEntity<EntityModel<GenreDTO>> getGenreByName(@PathVariable String genreName) {
        return genreService.getGenreByName(genreName);
    }

    @PostMapping
    public ResponseEntity<EntityModel<GenreDTO>> createGenre(@RequestBody GenreDTO genreDTO) {
        return genreService.createGenre(genreDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<GenreDTO>> updateGenre(@PathVariable Integer id, @RequestBody GenreDTO genreDTO) {
        return genreService.updateGenre(id, genreDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EntityModel<GenreDTO>> patchGenre(@PathVariable Integer id, @RequestBody GenreDTO genreDTO) {
        return genreService.patchGenre(id, genreDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Integer id) {
        return genreService.deleteGenre(id);
    }
}