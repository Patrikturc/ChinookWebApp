package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.dtos.AlbumDTO;
import com.sparta.pt.chinookwebapp.models.Album;
import com.sparta.pt.chinookwebapp.models.Artist;
import com.sparta.pt.chinookwebapp.repositories.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistService artistService;

    @Autowired
    public AlbumService(AlbumRepository albumRepository, ArtistService artistService) {
        this.albumRepository = albumRepository;
        this.artistService = artistService;
    }

    public List<AlbumDTO> getAllAlbums() {
        return albumRepository.findAll().stream()
                .map(album -> new AlbumDTO(album.getId(), album.getTitle(), album.getArtist().getName()))
                .collect(Collectors.toList());
    }

    public Optional<AlbumDTO> getAlbumById(Integer id) {
        return albumRepository.findById(id)
                .map(album -> new AlbumDTO(album.getId(), album.getTitle(), album.getArtist().getName()));
    }

    public Album createAlbum(Album album, String artistName) {
        Optional<Artist> artistOptional = artistService.getArtistByName(artistName);

        if (artistOptional.isPresent()) {
            album.setArtist(artistOptional.get());

            List<Album> allAlbums = albumRepository.findAll();
            int maxId = allAlbums.stream()
                    .max(Comparator.comparingInt(Album::getId))
                    .map(Album::getId)
                    .orElse(0);

            album.setId(maxId + 1);

            return albumRepository.save(album);
        } else {
            throw new IllegalArgumentException("Artist not found: " + artistName);
        }
    }

    public Optional<Album> updateAlbum(Integer id, Album albumDetails, String artistName) {
        return albumRepository.findById(id)
                .flatMap(existingAlbum -> {
                    existingAlbum.setTitle(albumDetails.getTitle());
                    Optional<Artist> artistOptional = artistService.getArtistByName(artistName);
                    if (artistOptional.isPresent()) {
                        existingAlbum.setArtist(artistOptional.get());
                        return Optional.of(albumRepository.save(existingAlbum));
                    } else {
                        return Optional.empty();
                    }
                });
    }

    public Optional<Album> patchAlbum(Integer id, AlbumDTO albumDetails, String artistName) {
        return albumRepository.findById(id)
                .flatMap(existingAlbum -> {
                    if (albumDetails.getTitle() != null) {
                        existingAlbum.setTitle(albumDetails.getTitle());
                    }
                    if (artistName != null) {
                        Optional<Artist> artistOptional = artistService.getArtistByName(artistName);
                        if (artistOptional.isPresent()) {
                            existingAlbum.setArtist(artistOptional.get());
                        } else {
                            return Optional.empty();
                        }
                    }
                    return Optional.of(albumRepository.save(existingAlbum));
                });
    }

    public boolean deleteAlbum(Integer id) {
        if (albumRepository.existsById(id)) {
            albumRepository.deleteById(id);
            return true;
        }
        return false;
    }
}