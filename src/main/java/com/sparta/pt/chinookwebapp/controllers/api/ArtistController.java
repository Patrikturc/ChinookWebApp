package com.sparta.pt.chinookwebapp.controllers.api;

import com.sparta.pt.chinookwebapp.assemblers.AlbumDTOAssembler;
import com.sparta.pt.chinookwebapp.assemblers.ArtistDTOAssembler;
import com.sparta.pt.chinookwebapp.dtos.ArtistDTO;
import com.sparta.pt.chinookwebapp.models.Artist;
import com.sparta.pt.chinookwebapp.services.ArtistService;
import com.sparta.pt.chinookwebapp.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/artists")
public class ArtistController {

    private final ArtistService artistService;
    private final PaginationUtils<ArtistDTO> paginationUtils;
    private final ArtistDTOAssembler artistDTOAssembler;
    private final AlbumDTOAssembler albumDTOAssembler;

    @Autowired
    public ArtistController(ArtistService artistService, PaginationUtils<ArtistDTO> paginationUtils, ArtistDTOAssembler artistDTOAssembler, AlbumDTOAssembler albumDTOAssembler) {
        this.artistService = artistService;
        this.paginationUtils = paginationUtils;
        this.artistDTOAssembler = artistDTOAssembler;
        this.albumDTOAssembler = albumDTOAssembler;
    }

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<ArtistDTO>>> getAllArtists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        Page<ArtistDTO> artistsPage = artistService.getAllArtists(page, size);
        PagedModel<EntityModel<ArtistDTO>> pagedResources = paginationUtils.createPagedResponse(
                artistsPage.map(artistDTOAssembler::toModel)).getBody();
        return ResponseEntity.ok(pagedResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistDTO> getArtistById(@PathVariable Integer id) {
        Optional<ArtistDTO> artist = artistService.getArtistById(id);
        return artist.map(dto -> ResponseEntity.ok(artistDTOAssembler.toModel(dto)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ArtistDTO> getArtistByName(@PathVariable String name) {
        Optional<ArtistDTO> artist = artistService.getArtistByName(name);
        return artist.map(dto -> ResponseEntity.ok(artistDTOAssembler.toModel(dto)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ArtistDTO> createArtist(@RequestBody ArtistDTO artistDTO) {
        ArtistDTO createdArtistDTO = artistService.createArtist(artistDTO);
        return ResponseEntity.ok(artistDTOAssembler.toModel(createdArtistDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArtistDTO> updateArtist(@PathVariable Integer id, @RequestBody ArtistDTO artistDetails) {
        Optional<ArtistDTO> updatedArtist = Optional.ofNullable(artistService.upsertArtist(id, artistDetails));
        return updatedArtist.map(dto -> ResponseEntity.ok(artistDTOAssembler.toModel(dto)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ArtistDTO> patchArtist(@PathVariable Integer id, @RequestBody Artist artistDetails) {
        Optional<ArtistDTO> updatedArtist = artistService.patchArtist(id, artistDetails);
        return updatedArtist.map(dto -> ResponseEntity.ok(artistDTOAssembler.toModel(dto)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable Integer id) {
        boolean isDeleted = artistService.deleteArtist(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}