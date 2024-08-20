package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.controllers.api.AlbumController;
import com.sparta.pt.chinookwebapp.controllers.api.ArtistController;
import com.sparta.pt.chinookwebapp.utils.HateoasUtils;
import com.sparta.pt.chinookwebapp.dtos.AlbumDTO;
import com.sparta.pt.chinookwebapp.models.Album;
import com.sparta.pt.chinookwebapp.models.Artist;
import com.sparta.pt.chinookwebapp.repositories.AlbumRepository;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilderFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class AlbumService extends BaseService<Album, AlbumDTO, AlbumRepository> {

    private final AlbumRepository albumRepository;
    private final ArtistService artistService;

    @Autowired
    public AlbumService(AlbumRepository albumRepository, ArtistService artistService, HateoasUtils<AlbumDTO> hateoasUtils, WebMvcLinkBuilderFactory linkBuilderFactory) {
        super(albumRepository, hateoasUtils, linkBuilderFactory);
        this.artistService = artistService;
        this.albumRepository = albumRepository;
    }

    public ResponseEntity<PagedModel<EntityModel<AlbumDTO>>> getAllAlbums(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return getAll(pageable, this::toDto, AlbumController.class, AlbumDTO::getId,
                (dto, linkBuilder) -> linkBuilderFactory.linkTo(ArtistController.class).slash("name").slash(dto.getArtistName()));
    }

    public ResponseEntity<EntityModel<AlbumDTO>> getAlbumById(Integer id) {
        return getById(id, this::toDto, AlbumController.class, AlbumDTO::getId,
                (dto, linkBuilder) -> linkBuilderFactory.linkTo(ArtistController.class).slash("name").slash(dto.getArtistName()));
    }

    public ResponseEntity<EntityModel<AlbumDTO>> getAlbumByTitle(String title) {
        Optional<Album> albumOptional = albumRepository.findByTitle(title);
        if (albumOptional.isPresent()) {
            Album album = albumOptional.get();
            AlbumDTO albumDTO = toDto(album);
            return hateoasUtils.createEntityResponse(albumDTO, AlbumController.class, AlbumDTO::getId,
                    (dto, linkBuilder) -> linkBuilderFactory.linkTo(ArtistController.class).slash("name").slash(dto.getArtistName()), "Artist");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<PagedModel<EntityModel<AlbumDTO>>> getAlbumsByArtistName(String artistName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Artist> artists = artistService.getArtistByName(artistName);

        if (artists.isEmpty()) {
            throw new IllegalArgumentException("Artist not found: " + artistName);
        }

        Page<Album> albums = repository.findByArtistIn(artists, pageable);
        Page<AlbumDTO> albumPage = albums.map(this::toDto);

        return hateoasUtils.createPagedResponseWithCustomLinks(
                albumPage,
                AlbumController.class,
                AlbumDTO::getId,
                Optional.of((dto, linkBuilder) -> linkBuilderFactory.linkTo(ArtistController.class).slash("name").slash(dto.getArtistName())),
                "artist"
        );
    }

    public ResponseEntity<EntityModel<AlbumDTO>> createAlbum(AlbumDTO albumDTO) {
        Album album = new Album();
        album.setTitle(albumDTO.getTitle());
        List<Artist> artists = artistService.getArtistByName(albumDTO.getArtistName());
        if (!artists.isEmpty()) {
            album.setArtist(artists.getFirst());
            return create(album, this::toDto, AlbumController.class, AlbumDTO::getId,
                    (dto, linkBuilder) -> linkBuilderFactory.linkTo(ArtistController.class).slash("name").slash(dto.getArtistName()));
        } else {
            throw new IllegalArgumentException("Artist not found: " + albumDTO.getArtistName());
        }
    }

    public ResponseEntity<EntityModel<AlbumDTO>> updateAlbum(Integer id, AlbumDTO albumDTO) {
        Album albumDetails = new Album();
        albumDetails.setTitle(albumDTO.getTitle());
        List<Artist> artists = artistService.getArtistByName(albumDTO.getArtistName());

        if (!artists.isEmpty()) {
            albumDetails.setArtist(artists.getFirst());
            return update(id, albumDetails, this::toDto, AlbumController.class, AlbumDTO::getId,
                    (dto, linkBuilder) -> linkBuilderFactory.linkTo(ArtistController.class).slash("name").slash(dto.getArtistName()));
        } else {
            throw new IllegalArgumentException("Artist not found: " + albumDTO.getArtistName());
        }
    }

    public ResponseEntity<EntityModel<AlbumDTO>> patchAlbum(Integer id, AlbumDTO albumDTO) {
        Optional<Album> patchedAlbum = repository.findById(id)
                .flatMap(existingAlbum -> {
                    if (albumDTO.getTitle() != null) {
                        existingAlbum.setTitle(albumDTO.getTitle());
                    }
                    if (albumDTO.getArtistName() != null) {
                        List<Artist> artists = artistService.getArtistByName(albumDTO.getArtistName());
                        if (!artists.isEmpty()) {
                            existingAlbum.setArtist(artists.getFirst());
                        } else {
                            return Optional.empty();
                        }
                    }
                    return Optional.of(repository.save(existingAlbum));
                });

        if (patchedAlbum.isPresent()) {
            return getAlbumById(patchedAlbum.get().getId());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<Void> deleteAlbum(Integer id) {
        return delete(id);
    }

    @Override
    protected void updateEntity(Album existingAlbum, Album albumDetails) {
        existingAlbum.setTitle(albumDetails.getTitle());
        existingAlbum.setArtist(albumDetails.getArtist());
    }

    private AlbumDTO toDto(Album album) {
        return new AlbumDTO(album.getId(), album.getTitle(), album.getArtist().getName());
    }
}