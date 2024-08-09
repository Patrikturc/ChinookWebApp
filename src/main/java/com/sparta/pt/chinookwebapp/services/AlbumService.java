package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.dtos.AlbumDTO;
import com.sparta.pt.chinookwebapp.models.Album;
import com.sparta.pt.chinookwebapp.models.Artist;
import com.sparta.pt.chinookwebapp.repositories.AlbumRepository;
import com.sparta.pt.chinookwebapp.repositories.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;

    @Autowired
    public AlbumService(AlbumRepository albumRepository, ArtistRepository artistRepository) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
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
        Optional<Artist> artistOptional = artistRepository.findByName(artistName);
        if (artistOptional.isPresent()) {
            album.setArtist(artistOptional.get());
            return albumRepository.save(album);
        } else {
            throw new IllegalArgumentException("Artist not found: " + artistName);
        }
    }

    public Optional<Album> updateAlbum(Integer id, Album albumDetails, String artistName) {
        return albumRepository.findById(id)
                .flatMap(existingAlbum -> {
                    existingAlbum.setTitle(albumDetails.getTitle());
                    Optional<Artist> artistOptional = artistRepository.findByName(artistName);
                    if (artistOptional.isPresent()) {
                        existingAlbum.setArtist(artistOptional.get());
                        return Optional.of(albumRepository.save(existingAlbum));
                    } else {
                        return Optional.empty();  // Artist not found
                    }
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