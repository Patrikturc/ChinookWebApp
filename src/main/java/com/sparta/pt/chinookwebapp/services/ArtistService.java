package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.controllers.api.ArtistController;
import com.sparta.pt.chinookwebapp.controllers.api.HateoasUtils;
import com.sparta.pt.chinookwebapp.dtos.ArtistDTO;
import com.sparta.pt.chinookwebapp.models.Artist;
import com.sparta.pt.chinookwebapp.repositories.ArtistRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilderFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArtistService extends BaseService<Artist, ArtistDTO, ArtistRepository> {

    @Autowired
    public ArtistService(ArtistRepository artistRepository, HateoasUtils<ArtistDTO> hateoasUtils, WebMvcLinkBuilderFactory linkBuilderFactory) {
        super(artistRepository, hateoasUtils, linkBuilderFactory);
    }

    public ResponseEntity<PagedModel<EntityModel<ArtistDTO>>> getAllArtists(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return getAll(pageable, this::toDto, ArtistController.class, ArtistDTO::getId,
                (dto, linkBuilder) -> linkBuilderFactory.linkTo(ArtistController.class).slash("name").slash(dto.getName()));
    }

    public ResponseEntity<EntityModel<ArtistDTO>> getArtistById(Integer id) {
        return getById(id, this::toDto, ArtistController.class, ArtistDTO::getId,
                (dto, linkBuilder) -> linkBuilderFactory.linkTo(ArtistController.class).slash("name").slash(dto.getName()));
    }

    public List<Artist> getArtistByName(String artistName) {
        String normalizedArtistName = sanitizeInput(artistName);
        return repository.findAll().stream()
                .filter(artist -> sanitizeInput(artist.getName()).contains(normalizedArtistName))
                .collect(Collectors.toList());
    }

    public ResponseEntity<PagedModel<EntityModel<ArtistDTO>>> getArtistByName(String artistName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        String normalizedArtistName = sanitizeInput(artistName);
        List<Artist> artists = repository.findAll().stream()
                .filter(artist -> sanitizeInput(artist.getName()).contains(normalizedArtistName))
                .collect(Collectors.toList());

        if (artists.isEmpty()) {
            throw new IllegalArgumentException("Artist not found: " + artistName);
        }

        Page<Artist> artistPage = new PageImpl<>(artists, pageable, artists.size());
        Page<ArtistDTO> artistDTOPage = artistPage.map(this::toDto);

        return hateoasUtils.createPagedResponseWithCustomLinks(artistDTOPage, ArtistController.class, ArtistDTO::getId,
                (dto, linkBuilder) -> linkBuilderFactory.linkTo(ArtistController.class).slash("name").slash(dto.getName()));
    }

    public ResponseEntity<EntityModel<ArtistDTO>> createArtist(ArtistDTO artistDTO) {
        Artist artist = new Artist();
        artist.setName(artistDTO.getName());

        return create(artist, this::toDto, ArtistController.class, ArtistDTO::getId,
                (dto, linkBuilder) -> linkBuilderFactory.linkTo(ArtistController.class).slash("name").slash(dto.getName()));
    }

    public ResponseEntity<EntityModel<ArtistDTO>> updateArtist(Integer id, ArtistDTO artistDTO) {
        Artist artistDetails = new Artist();
        artistDetails.setName(artistDTO.getName());

        return update(id, artistDetails, this::toDto, ArtistController.class, ArtistDTO::getId,
                (dto, linkBuilder) -> linkBuilderFactory.linkTo(ArtistController.class).slash("name").slash(dto.getName()));
    }

    public ResponseEntity<EntityModel<ArtistDTO>> patchArtist(Integer id, ArtistDTO artistDTO) {
        Optional<Artist> patchedArtist = repository.findById(id)
                .flatMap(existingArtist -> {
                    if (artistDTO.getName() != null) {
                        existingArtist.setName(artistDTO.getName());
                    }
                    return Optional.of(repository.save(existingArtist));
                });

        if (patchedArtist.isPresent()) {
            return getArtistById(patchedArtist.get().getId());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<Void> deleteArtist(Integer id) {
        return delete(id);
    }

    @Override
    protected void updateEntity(Artist existingArtist, Artist artistDetails) {
        existingArtist.setName(artistDetails.getName());
    }

    private ArtistDTO toDto(Artist artist) {
        return new ArtistDTO(artist.getId(), artist.getName());
    }

    private String sanitizeInput(String input) {
        return input.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
    }
}