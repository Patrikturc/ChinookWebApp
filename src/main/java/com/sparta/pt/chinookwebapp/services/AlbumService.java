package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.controllers.api.AlbumController;
import com.sparta.pt.chinookwebapp.controllers.api.ArtistController;
import com.sparta.pt.chinookwebapp.utils.HateoasUtils;
import com.sparta.pt.chinookwebapp.dtos.AlbumDTO;
import com.sparta.pt.chinookwebapp.models.Album;
import com.sparta.pt.chinookwebapp.models.Artist;
import com.sparta.pt.chinookwebapp.repositories.AlbumRepository;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilderFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlbumService extends BaseService<Album, AlbumDTO, AlbumRepository> {

    private final ArtistService artistService;

    @Autowired
    public AlbumService(AlbumRepository albumRepository,
                        ArtistService artistService,
                        HateoasUtils<AlbumDTO> hateoasUtils,
                        WebMvcLinkBuilderFactory linkBuilderFactory) {
        super(albumRepository, hateoasUtils, linkBuilderFactory);
        this.artistService = artistService;
    }

    public ResponseEntity<PagedModel<EntityModel<AlbumDTO>>> getAllAlbums(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Album> albums = repository.findAll(pageable);
        return getPagedModelResponseEntity(albums);
    }

    public ResponseEntity<EntityModel<AlbumDTO>> getAlbumById(Integer id) {
        return getById(id, this::toDto, AlbumController.class, AlbumDTO::getId);
    }

    public ResponseEntity<EntityModel<AlbumDTO>> getAlbumByTitle(String title) {
        Album album = repository.findByTitle(title)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("Album not found with title: " + title));
        AlbumDTO albumDTO = toDto(album);
        addCustomLinks(albumDTO);
        return createEntityResponse(albumDTO);
    }

    public ResponseEntity<PagedModel<EntityModel<AlbumDTO>>> getAlbumsByArtistName(String artistName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Artist> artists = artistService.getArtistByName(artistName);
        if (artists.isEmpty()) {
            throw new OpenApiResourceNotFoundException("Artist not found with name: " + artistName);
        }
        Page<Album> albums = repository.findByArtistIn(artists, pageable);
        return getPagedModelResponseEntity(albums);
    }

    public ResponseEntity<EntityModel<AlbumDTO>> createAlbum(AlbumDTO albumDTO) {
        Album album = convertToEntity(albumDTO);
        return create(album, this::toDto, AlbumController.class, AlbumDTO::getId);
    }

    public ResponseEntity<EntityModel<AlbumDTO>> updateAlbum(Integer id, AlbumDTO albumDTO) {
        Album albumDetails = convertToEntity(albumDTO);
        return update(id, albumDetails, this::toDto, AlbumController.class, AlbumDTO::getId);
    }

    public ResponseEntity<EntityModel<AlbumDTO>> patchAlbum(Integer id, AlbumDTO albumDTO) {
        Optional<Album> patchedAlbum = repository.findById(id)
                .map(existingAlbum -> {
                    if (albumDTO.getTitle() != null) existingAlbum.setTitle(albumDTO.getTitle());
                    if (albumDTO.getArtistName() != null) {
                        List<Artist> artists = artistService.getArtistByName(albumDTO.getArtistName());
                        if (!artists.isEmpty()) existingAlbum.setArtist(artists.getFirst());
                    }
                    return repository.save(existingAlbum);
                });
        return patchedAlbum.map(this::toDto).map(dto -> {
            addCustomLinks(dto);
            return createEntityResponse(dto);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<Void> deleteAlbum(Integer id) {
        return delete(id);
    }

    @Override
    protected void updateEntity(Album existingAlbum, Album albumDetails) {
        if (albumDetails.getTitle() != null) existingAlbum.setTitle(albumDetails.getTitle());
        if (albumDetails.getArtist() != null) existingAlbum.setArtist(albumDetails.getArtist());
    }

    private AlbumDTO toDto(Album album) {
        return new AlbumDTO(album.getId(), album.getTitle(), album.getArtist().getName());
    }

    private Album convertToEntity(AlbumDTO albumDTO) {
        Album album = new Album();
        album.setTitle(albumDTO.getTitle());
        List<Artist> artists = artistService.getArtistByName(albumDTO.getArtistName());
        if (!artists.isEmpty()) {
            album.setArtist(artists.getFirst());
        }
        return album;
    }

    private ResponseEntity<PagedModel<EntityModel<AlbumDTO>>> getPagedModelResponseEntity(Page<Album> albums) {
        Page<AlbumDTO> albumDTOs = albums.map(this::toDto);
        return hateoasUtils.createPagedResponse(albumDTOs, AlbumController.class, AlbumDTO::getId);
    }

    private ResponseEntity<EntityModel<AlbumDTO>> createEntityResponse(AlbumDTO albumDTO) {
        EntityModel<AlbumDTO> entityModel = EntityModel.of(albumDTO);
        WebMvcLinkBuilder selfLinkBuilder = WebMvcLinkBuilder.linkTo(AlbumController.class).slash(albumDTO.getId());
        entityModel.add(selfLinkBuilder.withSelfRel());
        return ResponseEntity.ok(entityModel);
    }

    private void addCustomLinks(AlbumDTO albumDTO) {
        if (albumDTO.getArtistName() != null) {
            String sanitizedArtistName = sanitizeInput(albumDTO.getArtistName());
            WebMvcLinkBuilder artistLink = WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(ArtistController.class).getArtistByName(sanitizedArtistName, 0, 10)
            );
            albumDTO.add(artistLink.withRel("artist"));
        }
    }

    private String sanitizeInput(String input) {
        return input.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
    }
}