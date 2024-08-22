package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.controllers.api.GenreController;
import com.sparta.pt.chinookwebapp.dtos.GenreDTO;
import com.sparta.pt.chinookwebapp.models.Genre;
import com.sparta.pt.chinookwebapp.repositories.GenreRepository;
import com.sparta.pt.chinookwebapp.utils.HateoasUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilderFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GenreService extends BaseService<Genre, GenreDTO, GenreRepository> {

    @Autowired
    public GenreService(GenreRepository genreRepository, HateoasUtils<GenreDTO> hateoasUtils, WebMvcLinkBuilderFactory linkBuilderFactory) {
        super(genreRepository, hateoasUtils, linkBuilderFactory);
    }

    public ResponseEntity<PagedModel<EntityModel<GenreDTO>>> getAllGenres(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return getAll(pageable, this::convertToDTO, GenreController.class, GenreDTO::getId);
    }

    public ResponseEntity<EntityModel<GenreDTO>> getGenreById(Integer id) {
        return getById(id, this::convertToDTO, GenreController.class, GenreDTO::getId);
    }

    public ResponseEntity<EntityModel<GenreDTO>> getGenreByName(String genreName) {
        String sanitizedGenreName = sanitizeInput(genreName);
        Optional<Genre> genre = repository.findAll().stream()
                .filter(g -> sanitizeInput(g.getName()).equals(sanitizedGenreName))
                .findFirst();

        if (genre.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        GenreDTO genreDTO = convertToDTO(genre.get());
        return hateoasUtils.createEntityResponse(
                genreDTO,
                GenreController.class,
                GenreDTO::getId
        );
    }

    public ResponseEntity<EntityModel<GenreDTO>> createGenre(GenreDTO genreDTO) {
        Genre genre = convertToEntity(genreDTO);
        setNextId(genre);

        Genre savedGenre = repository.save(genre);
        return getById(savedGenre.getId(), this::convertToDTO, GenreController.class, GenreDTO::getId);
    }

    public ResponseEntity<EntityModel<GenreDTO>> updateGenre(Integer id, GenreDTO genreDTO) {
        Genre genreDetails = convertToEntity(genreDTO);
        return update(id, genreDetails, this::convertToDTO, GenreController.class, GenreDTO::getId);
    }

    public ResponseEntity<EntityModel<GenreDTO>> patchGenre(Integer id, GenreDTO genreDTO) {
        Optional<Genre> patchedGenre = repository.findById(id)
                .map(existingGenre -> {
                    if (genreDTO.getName() != null) {
                        existingGenre.setName(genreDTO.getName());
                    }
                    return repository.save(existingGenre);
                });

        return patchedGenre.map(genre -> getById(genre.getId(), this::convertToDTO, GenreController.class, GenreDTO::getId))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<Void> deleteGenre(Integer id) {
        return delete(id);
    }

    @Override
    protected void updateEntity(Genre existingGenre, Genre genreDetails) {
        if (genreDetails.getName() != null) {
            existingGenre.setName(genreDetails.getName());
        }
    }

    @Override
    protected void updateEntityPartial(Genre existingEntity, GenreDTO dtoDetails) {
        if (dtoDetails.getName() != null) {
            existingEntity.setName(dtoDetails.getName());
        }
    }

    private GenreDTO convertToDTO(Genre genre) {
        return new GenreDTO(genre.getId(), genre.getName());
    }

    private Genre convertToEntity(GenreDTO genreDTO) {
        Genre genre = new Genre();
        genre.setId(genreDTO.getId());
        genre.setName(genreDTO.getName());
        return genre;
    }

    private String sanitizeInput(String input) {
        return input.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
    }
}
