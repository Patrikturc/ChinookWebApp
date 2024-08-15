package com.sparta.pt.chinookwebapp.controllers.api;

import com.sparta.pt.chinookwebapp.dtos.AlbumDTO;
import com.sparta.pt.chinookwebapp.models.Album;
import com.sparta.pt.chinookwebapp.services.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    private final AlbumService albumService;

    @Autowired
    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    public List<AlbumDTO> getAllAlbums() {
        return albumService.getAllAlbums();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumDTO> getAlbumById(@PathVariable Integer id) {
        Optional<AlbumDTO> album = albumService.getAlbumById(id);
        return album.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AlbumDTO> createAlbum(@RequestBody AlbumDTO albumDTO) {
        try {
            Album album = new Album();
            album.setTitle(albumDTO.getTitle());
            Album createdAlbum = albumService.createAlbum(album, albumDTO.getArtistName());

            return ResponseEntity.ok(new AlbumDTO(
                    createdAlbum.getId(),
                    createdAlbum.getTitle(),
                    createdAlbum.getArtist().getName()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlbumDTO> updateAlbum(@PathVariable Integer id, @RequestBody AlbumDTO albumDTO) {
        try {
            Album albumDetails = new Album();
            albumDetails.setTitle(albumDTO.getTitle());

            Optional<Album> updatedAlbum = albumService.updateAlbum(id, albumDetails, albumDTO.getArtistName());
            return updatedAlbum.map(a -> new AlbumDTO(a.getId(), a.getTitle(), a.getArtist().getName()))
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AlbumDTO> patchAlbum(@PathVariable Integer id, @RequestBody AlbumDTO albumDTO) {
        String artistName = albumDTO.getArtistName();
        Optional<Album> patchedAlbum = albumService.patchAlbum(id, albumDTO, artistName);

        if (patchedAlbum.isPresent()) {
            AlbumDTO patchedAlbumDTO = new AlbumDTO(
                    patchedAlbum.get().getId(),
                    patchedAlbum.get().getTitle(),
                    patchedAlbum.get().getArtist().getName()
            );
            return ResponseEntity.ok(patchedAlbumDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Integer id) {
        boolean isDeleted = albumService.deleteAlbum(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}