package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.controllers.api.AlbumController;
import com.sparta.pt.chinookwebapp.controllers.api.ArtistController;
import com.sparta.pt.chinookwebapp.controllers.api.HateoasUtils;
import com.sparta.pt.chinookwebapp.dtos.AlbumDTO;
import com.sparta.pt.chinookwebapp.models.Album;
import com.sparta.pt.chinookwebapp.models.Artist;
import com.sparta.pt.chinookwebapp.repositories.AlbumRepository;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilderFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistService artistService;
    private final HateoasUtils<AlbumDTO> hateoasUtils;
    private final WebMvcLinkBuilderFactory linkBuilderFactory;

    @Autowired
    public AlbumService(AlbumRepository albumRepository, ArtistService artistService, HateoasUtils<AlbumDTO> hateoasUtils, WebMvcLinkBuilderFactory linkBuilderFactory) {
        this.albumRepository = albumRepository;
        this.artistService = artistService;
        this.hateoasUtils = hateoasUtils;
        this.linkBuilderFactory = linkBuilderFactory;
    }

    public ResponseEntity<PagedModel<EntityModel<AlbumDTO>>> getAllAlbums(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Album> albums = albumRepository.findAll(pageable);
        Page<AlbumDTO> albumPage = albums.map(album -> new AlbumDTO(album.getId(), album.getTitle(), album.getArtist().getName()));
        return hateoasUtils.createPagedResponseWithCustomLinks(albumPage, AlbumController.class, AlbumDTO::getId,
                (dto, linkBuilder) -> linkBuilderFactory.linkTo(ArtistController.class).slash("name").slash(dto.getArtistName()));
    }

    public ResponseEntity<EntityModel<AlbumDTO>> getAlbumById(Integer id) {
        return albumRepository.findById(id)
                .map(album -> {
                    AlbumDTO albumDTO = new AlbumDTO(album.getId(), album.getTitle(), album.getArtist().getName());
                    return hateoasUtils.createEntityResponse(albumDTO, AlbumController.class, AlbumDTO::getId,
                            (dto, linkBuilder) -> linkBuilderFactory.linkTo(ArtistController.class).slash("name").slash(album.getArtist().getName()));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<PagedModel<EntityModel<AlbumDTO>>> getAlbumsByArtistName(String artistName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Artist> artists = artistService.getArtistByName(artistName);

        if (artists.isEmpty()) {
            throw new IllegalArgumentException("Artist not found: " + artistName);
        }

        Page<Album> albums = albumRepository.findByArtistIn(artists, pageable);
        Page<AlbumDTO> albumPage = albums.map(album -> new AlbumDTO(album.getId(), album.getTitle(), album.getArtist().getName()));

        return hateoasUtils.createPagedResponseWithCustomLinks(albumPage, AlbumController.class, AlbumDTO::getId,
                (dto, linkBuilder) -> linkBuilderFactory.linkTo(ArtistController.class).slash("name").slash(dto.getArtistName()));
    }

    public ResponseEntity<EntityModel<AlbumDTO>> createAlbum(AlbumDTO albumDTO) {
        try {
            Album album = new Album();
            album.setTitle(albumDTO.getTitle());
            List<Artist> artists = artistService.getArtistByName(albumDTO.getArtistName());

            if (!artists.isEmpty()) {
                album.setArtist(artists.get(0));

                List<Album> allAlbums = albumRepository.findAll();
                int maxId = allAlbums.stream()
                        .max(Comparator.comparingInt(Album::getId))
                        .map(Album::getId)
                        .orElse(0);

                album.setId(maxId + 1);

                Album createdAlbum = albumRepository.save(album);
                AlbumDTO createdAlbumDTO = new AlbumDTO(
                        createdAlbum.getId(),
                        createdAlbum.getTitle(),
                        createdAlbum.getArtist().getName()
                );

                return getAlbumById(createdAlbumDTO.getId());
            } else {
                throw new IllegalArgumentException("Artist not found: " + albumDTO.getArtistName());
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<EntityModel<AlbumDTO>> updateAlbum(Integer id, AlbumDTO albumDTO) {
        try {
            Album albumDetails = new Album();
            albumDetails.setTitle(albumDTO.getTitle());

            Optional<Album> updatedAlbum = albumRepository.findById(id)
                    .flatMap(existingAlbum -> {
                        existingAlbum.setTitle(albumDetails.getTitle());
                        List<Artist> artists = artistService.getArtistByName(albumDTO.getArtistName());
                        if (!artists.isEmpty()) {
                            existingAlbum.setArtist(artists.get(0));
                            return Optional.of(albumRepository.save(existingAlbum));
                        } else {
                            return Optional.empty();
                        }
                    });

            return updatedAlbum.map(a -> getAlbumById(a.getId()))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<EntityModel<AlbumDTO>> patchAlbum(Integer id, AlbumDTO albumDTO) {
        String artistName = albumDTO.getArtistName();
        Optional<Album> patchedAlbum = albumRepository.findById(id)
                .flatMap(existingAlbum -> {
                    if (albumDTO.getTitle() != null) {
                        existingAlbum.setTitle(albumDTO.getTitle());
                    }
                    if (artistName != null) {
                        List<Artist> artists = artistService.getArtistByName(artistName);
                        if (!artists.isEmpty()) {
                            existingAlbum.setArtist(artists.get(0));
                        } else {
                            return Optional.empty();
                        }
                    }
                    return Optional.of(albumRepository.save(existingAlbum));
                });

        if (patchedAlbum.isPresent()) {
            return getAlbumById(patchedAlbum.get().getId());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<Void> deleteAlbum(Integer id) {
        if (albumRepository.existsById(id)) {
            albumRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}