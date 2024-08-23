// TrackService.java
package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.converters.TrackDtoConverter;
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
import com.sparta.pt.chinookwebapp.utils.IdManagementUtils;
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
    private final TrackDtoConverter trackDtoConverter;
    private final IdManagementUtils idManagementUtils;

    @Autowired
    public TrackService(TrackRepository trackRepository, TrackDtoConverter trackDtoConverter, IdManagementUtils idManagementUtils) {
        this.trackRepository = trackRepository;
        this.trackDtoConverter = trackDtoConverter;
        this.idManagementUtils = idManagementUtils;
    }

    public Page<TrackDTO> getAllTracks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Track> tracks = trackRepository.findAll(pageable);
        return tracks.map(trackDtoConverter::convertToDTO);
    }

    public Page<TrackDTO> getTracksByGenreName(String genreName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Track> tracks = trackRepository.findByGenreNameContainingIgnoreCase(genreName, pageable);
        return tracks.map(trackDtoConverter::convertToDTO);
    }

    public Page<TrackDTO> getTracksByAlbumTitle(String albumTitle, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Track> tracks = trackRepository.findByAlbumTitleContainingIgnoreCase(albumTitle, pageable);
        return tracks.map(trackDtoConverter::convertToDTO);
    }

    public Optional<TrackDTO> getTrackById(int id) {
        return trackRepository.findById(id).map(trackDtoConverter::convertToDTO);
    }

    public TrackDTO createTrack(TrackDTO trackDTO) {
        Track track = trackDtoConverter.convertToEntity(trackDTO);

        List<Track> allTracks = trackRepository.findAll();
        int newId = idManagementUtils.generateId(allTracks, Track::getId);
        track.setId(newId);

        Track savedTrack = trackRepository.save(track);
        return trackDtoConverter.convertToDTO(savedTrack);
    }

    public Optional<TrackDTO> updateTrack(int id, TrackDTO trackDTO) {
        return trackRepository.findById(id).map(existingTrack -> {
            if (trackDTO.getName() != null) existingTrack.setName(trackDTO.getName());
            if (trackDTO.getComposer() != null) existingTrack.setComposer(trackDTO.getComposer());
            if (trackDTO.getMilliseconds() != null) existingTrack.setMilliseconds(trackDTO.getMilliseconds());
            if (trackDTO.getBytes() != null) existingTrack.setBytes(trackDTO.getBytes());
            if (trackDTO.getUnitPrice() != null) existingTrack.setUnitPrice(trackDTO.getUnitPrice());
            if (trackDTO.getAlbumTitle() != null) trackDtoConverter.setAlbumByTitle(existingTrack, trackDTO.getAlbumTitle());
            if (trackDTO.getMediaTypeName() != null) trackDtoConverter.setMediaTypeByName(existingTrack, trackDTO.getMediaTypeName());
            if (trackDTO.getGenreName() != null) trackDtoConverter.setGenreByName(existingTrack, trackDTO.getGenreName());

            Track updatedTrack = trackRepository.save(existingTrack);
            return trackDtoConverter.convertToDTO(updatedTrack);
        });
    }

    public TrackDTO upsertTrack(int id, TrackDTO trackDTO) {
        Track track = trackDtoConverter.convertToEntity(trackDTO);
        track.setId(id);

        if (!trackRepository.existsById(id)) {
            Track savedTrack = trackRepository.save(track);
            return trackDtoConverter.convertToDTO(savedTrack);
        }

        return updateTrack(id, trackDTO).orElse(null);
    }

    public boolean deleteTrack(int id) {
        if (trackRepository.existsById(id)) {
            trackRepository.deleteById(id);
            return true;
        }
        return false;
    }
}