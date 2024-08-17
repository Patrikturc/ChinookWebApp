// TrackService.java
package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.dtos.TrackDTO;
import com.sparta.pt.chinookwebapp.models.Album;
import com.sparta.pt.chinookwebapp.models.Genre;
import com.sparta.pt.chinookwebapp.models.Mediatype;
import com.sparta.pt.chinookwebapp.models.Mediatype;
import com.sparta.pt.chinookwebapp.models.Track;
import com.sparta.pt.chinookwebapp.repositories.AlbumRepository;
import com.sparta.pt.chinookwebapp.repositories.GenreRepository;
import com.sparta.pt.chinookwebapp.repositories.MediatypeRepository;
import com.sparta.pt.chinookwebapp.repositories.TrackRepository;
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
public class TrackService {

    private final TrackRepository trackRepository;
    private final AlbumRepository albumRepository;
    private final MediatypeRepository mediaTypeRepository;
    private final GenreRepository genreRepository;

    @Autowired
    public TrackService(TrackRepository trackRepository, AlbumRepository albumRepository, MediatypeRepository mediaTypeRepository, GenreRepository genreRepository) {
        this.trackRepository = trackRepository;
        this.albumRepository = albumRepository;
        this.mediaTypeRepository = mediaTypeRepository;
        this.genreRepository = genreRepository;
    }

    public Page<TrackDTO> getAllTracks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Track> tracks = trackRepository.findAll(pageable);
        return tracks.map(this::convertToDTO);
    }

    public Page<TrackDTO> getTracksByGenreName(String genreName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Track> tracks = trackRepository.findByGenreNameContainingIgnoreCase(genreName, pageable);
        return tracks.map(this::convertToDTO);
    }

    public Page<TrackDTO> getTracksByAlbumTitle(String albumTitle, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Track> tracks = trackRepository.findByAlbumTitleContainingIgnoreCase(albumTitle, pageable);
        return tracks.map(this::convertToDTO);
    }

    public Optional<TrackDTO> getTrackById(int id) {
        return trackRepository.findById(id).map(this::convertToDTO);
    }

    public TrackDTO createTrack(TrackDTO trackDTO) {
        Track track = convertToEntity(trackDTO);

        int maxId = trackRepository.findAll().stream()
                .max(Comparator.comparingInt(Track::getId))
                .map(Track::getId)
                .orElse(0);
        track.setId(maxId + 1);

        setAlbumByTitle(track, trackDTO.getAlbumTitle());
        setMediaTypeByName(track, trackDTO.getMediaTypeName());
        setGenreByName(track, trackDTO.getGenreName());

        Track savedTrack = trackRepository.save(track);
        return convertToDTO(savedTrack);
    }

    public Optional<TrackDTO> updateTrack(int id, TrackDTO trackDTO) {
        return trackRepository.findById(id).map(existingTrack -> {
            if (trackDTO.getName() != null) existingTrack.setName(trackDTO.getName());
            if (trackDTO.getComposer() != null) existingTrack.setComposer(trackDTO.getComposer());
            if (trackDTO.getMilliseconds() != null) existingTrack.setMilliseconds(trackDTO.getMilliseconds());
            if (trackDTO.getBytes() != null) existingTrack.setBytes(trackDTO.getBytes());
            if (trackDTO.getUnitPrice() != null) existingTrack.setUnitPrice(trackDTO.getUnitPrice());

            setAlbumByTitle(existingTrack, trackDTO.getAlbumTitle());
            setMediaTypeByName(existingTrack, trackDTO.getMediaTypeName());
            setGenreByName(existingTrack, trackDTO.getGenreName());

            Track updatedTrack = trackRepository.save(existingTrack);
            return convertToDTO(updatedTrack);
        });
    }

    public TrackDTO upsertTrack(int id, TrackDTO trackDTO) {
        Track track = convertToEntity(trackDTO);
        track.setId(id);

        setAlbumByTitle(track, trackDTO.getAlbumTitle());
        setMediaTypeByName(track, trackDTO.getMediaTypeName());
        setGenreByName(track, trackDTO.getGenreName());

        if (!trackRepository.existsById(id)) {
            Track savedTrack = trackRepository.save(track);
            return convertToDTO(savedTrack);
        }

        updateTrack(id, trackDTO);
        return convertToDTO(track);
    }

    public boolean deleteTrack(int id) {
        if (trackRepository.existsById(id)) {
            trackRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void setAlbumByTitle(Track track, String albumTitle) {
        if (albumTitle != null && !albumTitle.isEmpty()) {
            Optional<Album> album = albumRepository.findAll().stream()
                    .filter(a -> a.getTitle().equalsIgnoreCase(albumTitle))
                    .findFirst();
            album.ifPresent(track::setAlbum);
        }
    }

    private void setMediaTypeByName(Track track, String mediaTypeName) {
        if (mediaTypeName != null && !mediaTypeName.isEmpty()) {
            Optional<Mediatype> mediaType = mediaTypeRepository.findAll().stream()
                    .filter(mt -> mt.getName().equalsIgnoreCase(mediaTypeName))
                    .findFirst();
            mediaType.ifPresent(track::setMediaType);
        }
    }

    private void setGenreByName(Track track, String genreName) {
        if (genreName != null && !genreName.isEmpty()) {
            Optional<Genre> genre = genreRepository.findAll().stream()
                    .filter(g -> g.getName().equalsIgnoreCase(genreName))
                    .findFirst();
            genre.ifPresent(track::setGenre);
        }
    }

    private TrackDTO convertToDTO(Track track) {
        TrackDTO dto = new TrackDTO();
        dto.setId(track.getId());
        dto.setName(track.getName());
        dto.setComposer(track.getComposer());
        dto.setMilliseconds(track.getMilliseconds());
        dto.setBytes(track.getBytes());
        dto.setUnitPrice(track.getUnitPrice());
        dto.setAlbumTitle(track.getAlbum() != null ? track.getAlbum().getTitle() : null);
        dto.setMediaTypeName(track.getMediaType() != null ? track.getMediaType().getName() : null);
        dto.setGenreName(track.getGenre() != null ? track.getGenre().getName() : null);
        return dto;
    }

    private Track convertToEntity(TrackDTO trackDTO) {
        Track track = new Track();
        track.setId(trackDTO.getId());
        track.setName(trackDTO.getName());
        track.setComposer(trackDTO.getComposer());
        track.setMilliseconds(trackDTO.getMilliseconds());
        track.setBytes(trackDTO.getBytes());
        track.setUnitPrice(trackDTO.getUnitPrice());
        return track;
    }
}