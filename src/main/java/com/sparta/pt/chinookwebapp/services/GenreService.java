package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.controllers.api.GenreController;
import com.sparta.pt.chinookwebapp.dtos.GenreDTO;
import com.sparta.pt.chinookwebapp.models.Genre;
import com.sparta.pt.chinookwebapp.repositories.GenreRepository;
import com.sparta.pt.chinookwebapp.utils.HateoasUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilderFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GenreService extends BaseService<Genre, GenreDTO, GenreRepository> {

    @Autowired
    public GenreService(GenreRepository genreRepository, HateoasUtils<GenreDTO> hateoasUtils, WebMvcLinkBuilderFactory linkBuilderFactory) {
        super(genreRepository, hateoasUtils, linkBuilderFactory);
    }

    public ResponseEntity<PagedModel<EntityModel<GenreDTO>>> getAllGenres(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return getAll(pageable, this::convertToDTO, GenreController.class, GenreDTO::getId,
                (dto, linkBuilder) -> linkBuilderFactory.linkTo(GenreController.class).slash("name").slash(dto.getName()));
    }

    public ResponseEntity<EntityModel<GenreDTO>> getGenreById(Integer id) {
        return getById(id, this::convertToDTO, GenreController.class, GenreDTO::getId,
                (dto, linkBuilder) -> linkBuilderFactory.linkTo(GenreController.class).slash("name").slash(dto.getName()));
    }

    public ResponseEntity<EntityModel<GenreDTO>> getGenreByName(String genreName) {
        String normalizedGenreName = sanitizeInput(genreName);
        Optional<Genre> genre = repository.findAll().stream()
                .filter(g -> sanitizeInput(g.getName()).contains(normalizedGenreName))
                .findFirst();

        if (genre.isEmpty()) {
            throw new IllegalArgumentException("Genre not found: " + genreName);
        }

        GenreDTO genreDTO = convertToDTO(genre.get());
        return hateoasUtils.createEntityResponse(
                genreDTO,
                GenreController.class,
                GenreDTO::getId,
                (dto, linkBuilder) -> linkBuilderFactory.linkTo(GenreController.class).slash("name").slash(dto.getName()),
                "genre"
        );
    }

    public ResponseEntity<EntityModel<GenreDTO>> createGenre(GenreDTO genreDTO) {
        Genre genre = convertToEntity(genreDTO);
        List<Genre> allGenres = repository.findAll();

        int maxId = allGenres.stream()
                .max(Comparator.comparingInt(Genre::getId))
                .map(Genre::getId)
                .orElse(0);
        genre.setId(maxId + 1);

        return create(genre, this::convertToDTO, GenreController.class, GenreDTO::getId,
                (dto, linkBuilder) -> linkBuilderFactory.linkTo(GenreController.class).slash("name").slash(dto.getName()));
    }

    public ResponseEntity<EntityModel<GenreDTO>> updateGenre(Integer id, GenreDTO genreDTO) {
        Genre genreDetails = convertToEntity(genreDTO);
        return update(id, genreDetails, this::convertToDTO, GenreController.class, GenreDTO::getId,
                (dto, linkBuilder) -> linkBuilderFactory.linkTo(GenreController.class).slash("name").slash(dto.getName()));
    }

    public ResponseEntity<EntityModel<GenreDTO>> patchGenre(Integer id, GenreDTO genreDTO) {
        Optional<Genre> patchedGenre = repository.findById(id)
                .flatMap(existingGenre -> {
                    if (genreDTO.getName() != null) {
                        existingGenre.setName(genreDTO.getName());
                    }
                    return Optional.of(repository.save(existingGenre));
                });

        if (patchedGenre.isPresent()) {
            return getGenreById(patchedGenre.get().getId());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<Void> deleteGenre(Integer id) {
        return delete(id);
    }

    @Override
    protected void updateEntity(Genre existingGenre, Genre genreDetails) {
        existingGenre.setName(genreDetails.getName());
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