package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.dtos.AlbumDTO;
import com.sparta.pt.chinookwebapp.models.Album;
import com.sparta.pt.chinookwebapp.models.Artist;
import com.sparta.pt.chinookwebapp.models.Track;
import com.sparta.pt.chinookwebapp.repositories.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public Page<AlbumDTO> getAllAlbums(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<Album> albums = albumRepository.findAll(pageable);
        return albums.map(this::convertToDTO);
    }

    private AlbumDTO convertToDTO(Album album) {
        return new AlbumDTO(album.getId(), album.getTitle(), album.getArtist().getName());
    }

    public Optional<AlbumDTO> getAlbumById(Integer id) {
        return albumRepository.findById(id)
                .map(album -> new AlbumDTO(album.getId(), album.getTitle(), album.getArtist().getName()));
    }

    public Optional<AlbumDTO> getAlbumByTitle(String title) {
        return albumRepository.findByTitle(title)
                .map(album -> new AlbumDTO(album.getId(), album.getTitle(), album.getArtist().getName()));
    }

    public AlbumDTO createAlbum(AlbumDTO albumDTO) {
        Optional<Artist> artistOptional = artistService.getArtistByName(albumDTO.getArtistName());

        if (artistOptional.isPresent()) {
            Album album = new Album();
            album.setTitle(albumDTO.getTitle());
            album.setArtist(artistOptional.get());

            List<Album> allAlbums = albumRepository.findAll();
            int maxId = allAlbums.stream()
                    .max(Comparator.comparingInt(Album::getId))
                    .map(Album::getId)
                    .orElse(0);

            album.setId(maxId + 1);

            Album createdAlbum = albumRepository.save(album);
            return new AlbumDTO(
                    createdAlbum.getId(),
                    createdAlbum.getTitle(),
                    createdAlbum.getArtist().getName()
            );
        } else {
            throw new IllegalArgumentException("Artist not found: " + albumDTO.getArtistName());
        }
    }

    public Optional<AlbumDTO> updateAlbum(Integer id, AlbumDTO albumDTO) {
        return albumRepository.findById(id)
                .flatMap(existingAlbum -> {
                    existingAlbum.setTitle(albumDTO.getTitle());
                    Optional<Artist> artistOptional = artistService.getArtistByName(albumDTO.getArtistName());
                    if (artistOptional.isPresent()) {
                        existingAlbum.setArtist(artistOptional.get());
                        Album updatedAlbum = albumRepository.save(existingAlbum);
                        return Optional.of(new AlbumDTO(
                                updatedAlbum.getId(),
                                updatedAlbum.getTitle(),
                                updatedAlbum.getArtist().getName()
                        ));
                    } else {
                        return Optional.empty();
                    }
                });
    }

    public Optional<AlbumDTO> patchAlbum(Integer id, AlbumDTO albumDTO) {
        return albumRepository.findById(id)
                .flatMap(existingAlbum -> {
                    if (albumDTO.getTitle() != null) {
                        existingAlbum.setTitle(albumDTO.getTitle());
                    }
                    if (albumDTO.getArtistName() != null) {
                        Optional<Artist> artistOptional = artistService.getArtistByName(albumDTO.getArtistName());
                        if (artistOptional.isPresent()) {
                            existingAlbum.setArtist(artistOptional.get());
                        } else {
                            return Optional.empty();
                        }
                    }
                    Album patchedAlbum = albumRepository.save(existingAlbum);
                    return Optional.of(new AlbumDTO(
                            patchedAlbum.getId(),
                            patchedAlbum.getTitle(),
                            patchedAlbum.getArtist().getName()
                    ));
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