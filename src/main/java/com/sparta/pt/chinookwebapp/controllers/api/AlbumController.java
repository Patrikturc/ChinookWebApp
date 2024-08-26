package com.sparta.pt.chinookwebapp.controllers.api;

import com.sparta.pt.chinookwebapp.assemblers.AlbumDTOAssembler;
import com.sparta.pt.chinookwebapp.dtos.AlbumDTO;
import com.sparta.pt.chinookwebapp.services.AlbumService;
import com.sparta.pt.chinookwebapp.utils.CustomPagedResponse;
import com.sparta.pt.chinookwebapp.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    private final AlbumService albumService;
    private final PaginationUtils<AlbumDTO> paginationUtils;
    private final AlbumDTOAssembler albumDTOAssembler;

    @Autowired
    public AlbumController(AlbumService albumService, PaginationUtils<AlbumDTO> paginationUtils, AlbumDTOAssembler albumDTOAssembler) {
        this.albumService = albumService;
        this.paginationUtils = paginationUtils;
        this.albumDTOAssembler = albumDTOAssembler;
    }

    @GetMapping
    public ResponseEntity<CustomPagedResponse<AlbumDTO>> getAllAlbums(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {

        Page<AlbumDTO> albumsPage = albumService.getAllAlbums(page, size);
        return paginationUtils.createPagedResponse(albumsPage.map(albumDTOAssembler::toModel));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlbumDTO> getAlbumById(@PathVariable Integer id) {
        Optional<AlbumDTO> albumDTO = albumService.getAlbumById(id);
        return albumDTO.map(dto -> ResponseEntity.ok(albumDTOAssembler.toModel(dto)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<AlbumDTO> getAlbumByTitle(@PathVariable String title) {
        Optional<AlbumDTO> albumDTO = albumService.getAlbumByTitle(title);
        return albumDTO.map(dto -> ResponseEntity.ok(albumDTOAssembler.toModel(dto)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AlbumDTO> createAlbum(@RequestBody AlbumDTO albumDTO) {
            AlbumDTO createdAlbumDTO = albumService.createAlbum(albumDTO);
            return ResponseEntity.ok(albumDTOAssembler.toModel(createdAlbumDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlbumDTO> updateAlbum(@PathVariable Integer id, @RequestBody AlbumDTO albumDTO) {
        Optional<AlbumDTO> updatedAlbum = albumService.upsertAlbum(id, albumDTO);
        return updatedAlbum.map(dto -> ResponseEntity.ok(albumDTOAssembler.toModel(dto)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AlbumDTO> patchAlbum(@PathVariable Integer id, @RequestBody AlbumDTO albumDTO) {
        Optional<AlbumDTO> updatedAlbum = albumService.patchAlbum(id, albumDTO);
        return updatedAlbum.map(dto -> ResponseEntity.ok(albumDTOAssembler.toModel(dto)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Integer id) {
        boolean isDeleted = albumService.deleteAlbum(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}