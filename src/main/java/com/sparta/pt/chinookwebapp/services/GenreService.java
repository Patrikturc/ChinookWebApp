package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.converters.GenreDTOConverter;
import com.sparta.pt.chinookwebapp.dtos.GenreDTO;
import com.sparta.pt.chinookwebapp.models.Genre;
import com.sparta.pt.chinookwebapp.repositories.GenreRepository;
import com.sparta.pt.chinookwebapp.utils.IdManagementUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GenreService {

    private final GenreRepository genreRepository;
    private final GenreDTOConverter genreDtoConverter;
    private final IdManagementUtils idManagementUtils;

    @Autowired
    public GenreService(GenreRepository genreRepository, GenreDTOConverter genreDtoConverter, IdManagementUtils idManagementUtils) {
        this.genreRepository = genreRepository;
        this.genreDtoConverter = genreDtoConverter;
        this.idManagementUtils = idManagementUtils;
    }

    public List<GenreDTO> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(genreDtoConverter::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<GenreDTO> getGenreById(Integer id) {
        return genreRepository.findById(id)
                .map(genreDtoConverter::convertToDTO);
    }

    public Optional<GenreDTO> getGenreByName(String name) {
        return genreRepository.findByName(name)
                .map(genreDtoConverter::convertToDTO);
    }

    public GenreDTO createGenre(GenreDTO genreDTO) {
        Genre genre = genreDtoConverter.convertToEntity(genreDTO);

        List<Genre> allGenres = genreRepository.findAll();
        int newId = idManagementUtils.generateId(allGenres, Genre::getId);
        genre.setId(newId);

        Genre savedGenre = genreRepository.save(genre);
        return genreDtoConverter.convertToDTO(savedGenre);
    }

    public GenreDTO upsertGenre(Integer id, GenreDTO genreDTO) {
        Genre genre = genreRepository.findById(id).orElse(new Genre());
        genre.setId(id);
        genre.setName(genreDTO.getName());

        Genre savedGenre = genreRepository.save(genre);
        return genreDtoConverter.convertToDTO(savedGenre);
    }

    public Optional<GenreDTO> patchGenre(Integer id, GenreDTO genreDTO) {
        return genreRepository.findById(id)
                .map(existingGenre -> {
                    if (genreDTO.getName() != null) {
                        existingGenre.setName(genreDTO.getName());
                    }
                    Genre savedGenre = genreRepository.save(existingGenre);
                    return genreDtoConverter.convertToDTO(savedGenre);
                });
    }

    public boolean deleteGenre(Integer id) {
        if (genreRepository.existsById(id)) {
            genreRepository.deleteById(id);
            return true;
        }
        return false;
    }
}