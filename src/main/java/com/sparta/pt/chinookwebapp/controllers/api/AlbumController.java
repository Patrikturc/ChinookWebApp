package com.sparta.pt.chinookwebapp.controllers.api;

import com.sparta.pt.chinookwebapp.dtos.AlbumDTO;
import com.sparta.pt.chinookwebapp.services.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    private final AlbumService albumService;

    @Autowired
    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<AlbumDTO>>> getAllAlbums(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        return albumService.getAllAlbums(page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<AlbumDTO>> getAlbumById(@PathVariable Integer id) {
        return albumService.getAlbumById(id);
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<EntityModel<AlbumDTO>> getAlbumByTitle(@PathVariable String title) {
        return albumService.getAlbumByTitle(title);
    }

    @GetMapping("/artists/{artistName}")
    public ResponseEntity<PagedModel<EntityModel<AlbumDTO>>> getAlbumsByArtistName(
            @PathVariable String artistName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        return albumService.getAlbumsByArtistName(artistName, page, size);
    }

    @PostMapping
    public ResponseEntity<EntityModel<AlbumDTO>> createAlbum(@RequestBody AlbumDTO albumDTO) {
        return albumService.createAlbum(albumDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<AlbumDTO>> updateAlbum(@PathVariable Integer id, @RequestBody AlbumDTO albumDTO) {
        return albumService.updateAlbum(id, albumDTO);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EntityModel<AlbumDTO>> patchAlbum(@PathVariable Integer id, @RequestBody AlbumDTO albumDTO) {
        return albumService.patchAlbum(id, albumDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Integer id) {
        return albumService.deleteAlbum(id);
    }
}