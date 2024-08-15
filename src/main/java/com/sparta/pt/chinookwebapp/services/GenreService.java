package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.dtos.GenreDTO;
import com.sparta.pt.chinookwebapp.models.Genre;
import com.sparta.pt.chinookwebapp.repositories.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public List<GenreDTO> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<GenreDTO> getGenreById(Integer id) {
        return genreRepository.findById(id)
                .map(this::convertToDTO);
    }

    public GenreDTO createGenre(GenreDTO genreDTO) {
        Genre genre = convertToEntity(genreDTO);
        List<Genre> allGenres = genreRepository.findAll();

        int maxId = allGenres.stream()
                .max(Comparator.comparingInt(Genre::getId))
                .map(Genre::getId)
                .orElse(0);
        genre.setId(maxId + 1);

        Genre savedGenre = genreRepository.save(genre);
        return convertToDTO(savedGenre);
    }

    public GenreDTO upsertGenre(Integer id, GenreDTO genreDTO) {
        Genre genre = genreRepository.findById(id).orElse(new Genre());
        genre.setId(id);
        genre.setName(genreDTO.getName());

        Genre savedGenre = genreRepository.save(genre);
        return convertToDTO(savedGenre);
    }

    public Optional<GenreDTO> patchGenre(Integer id, GenreDTO genreDTO) {
        return genreRepository.findById(id)
                .map(existingGenre -> {
                    if (genreDTO.getName() != null) {
                        existingGenre.setName(genreDTO.getName());
                    }
                    Genre savedGenre = genreRepository.save(existingGenre);
                    return convertToDTO(savedGenre);
                });
    }

    public boolean deleteGenre(Integer id) {
        if (genreRepository.existsById(id)) {
            genreRepository.deleteById(id);
            return true;
        }
        return false;
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
}