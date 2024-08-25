package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.converters.PlaylistTrackDTOConverter;
import com.sparta.pt.chinookwebapp.dtos.PlaylistTrackDTO;
import com.sparta.pt.chinookwebapp.models.Playlist;
import com.sparta.pt.chinookwebapp.models.Playlisttrack;
import com.sparta.pt.chinookwebapp.models.PlaylisttrackId;
import com.sparta.pt.chinookwebapp.models.Track;
import com.sparta.pt.chinookwebapp.repositories.PlaylistRepository;
import com.sparta.pt.chinookwebapp.repositories.PlaylisttrackRepository;
import com.sparta.pt.chinookwebapp.repositories.TrackRepository;
import com.sparta.pt.chinookwebapp.utils.IdManagementUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlaylistTrackService {

    private final PlaylisttrackRepository playlisttrackRepository;
    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;
    private final PlaylistTrackDTOConverter playlistTrackDtoConverter;

    @Autowired
    public PlaylistTrackService(PlaylisttrackRepository playlisttrackRepository, PlaylistRepository playlistRepository, TrackRepository trackRepository, PlaylistTrackDTOConverter playlistTrackDtoConverter) {
        this.playlisttrackRepository = playlisttrackRepository;
        this.playlistRepository = playlistRepository;
        this.trackRepository = trackRepository;
        this.playlistTrackDtoConverter = playlistTrackDtoConverter;
    }

    public Page<PlaylistTrackDTO> getAllPlaylistTracks(int page, int size) {
        Page<Playlisttrack> playlistTracks = playlisttrackRepository.findAll(PageRequest.of(page, size));
        return playlistTracks.map(playlistTrackDtoConverter::convertToDTO);
    }

    public Page<PlaylistTrackDTO> getPlaylistTracksByName(String playlistName, int page, int size) {
        List<Playlist> playlists = playlistRepository.findAllByName(playlistName);
        if (playlists.isEmpty()) {
            return Page.empty();
        }
        List<Integer> playlistIds = playlists.stream()
                .map(Playlist::getId)
                .toList();
        List<Playlisttrack> playlistTracks = playlisttrackRepository.findAll().stream()
                .filter(playlisttrack -> playlistIds.contains(playlisttrack.getPlaylist().getId()))
                .toList();
        List<PlaylistTrackDTO> results = playlistTracks.stream()
                .map(playlistTrackDtoConverter::convertToDTO)
                .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), results.size());
        List<PlaylistTrackDTO> pagedList = results.subList(start, end);

        return new PageImpl<>(pagedList, pageable, results.size());
    }

    public Optional<PlaylistTrackDTO> getPlaylistTrackById(String playlistName, String trackName) {
        Optional<Playlist> playlist = playlistRepository.findByName(playlistName);
        Optional<Track> track = trackRepository.findByName(trackName);
        if (playlist.isPresent() && track.isPresent()) {
            PlaylisttrackId id = new PlaylisttrackId();
            id.setPlaylistId(playlist.get().getId());
            id.setTrackId(track.get().getId());
            return playlisttrackRepository.findById(id).map(playlistTrackDtoConverter::convertToDTO);
        }
        return Optional.empty();
    }

    public PlaylistTrackDTO createPlaylistTrack(PlaylistTrackDTO playlistTrackDTO) {
        Playlisttrack playlistTrack = playlistTrackDtoConverter.convertToEntity(playlistTrackDTO);

        setAssociations(playlistTrack, playlistTrackDTO);

        Playlisttrack savedPlaylistTrack = playlisttrackRepository.save(playlistTrack);
        return playlistTrackDtoConverter.convertToDTO(savedPlaylistTrack);
    }

    public PlaylistTrackDTO upsertPlaylistTrack(String playlistName, String trackName, PlaylistTrackDTO playlistTrackDTO) {
        Optional<Playlist> playlist = playlistRepository.findByName(playlistName);
        Optional<Track> track = trackRepository.findByName(trackName);
        if (playlist.isPresent() && track.isPresent()) {
            PlaylisttrackId id = new PlaylisttrackId();
            id.setPlaylistId(playlist.get().getId());
            id.setTrackId(track.get().getId());
            Playlisttrack playlistTrack = playlistTrackDtoConverter.convertToEntity(playlistTrackDTO);
            playlistTrack.setId(id);

            setAssociations(playlistTrack, playlistTrackDTO);

            if (!playlisttrackRepository.existsById(id)) {
                Playlisttrack savedPlaylistTrack = playlisttrackRepository.save(playlistTrack);
                return playlistTrackDtoConverter.convertToDTO(savedPlaylistTrack);
            }

            updatePlaylistTrack(playlistName, trackName, playlistTrackDTO);
            return playlistTrackDtoConverter.convertToDTO(playlistTrack);
        }
        return null;
    }

    public Optional<PlaylistTrackDTO> updatePlaylistTrack(String playlistName, String trackName, PlaylistTrackDTO playlistTrackDTO) {
        Optional<Playlist> playlist = playlistRepository.findByName(playlistName);
        Optional<Track> track = trackRepository.findByName(trackName);
        if (playlist.isPresent() && track.isPresent()) {
            PlaylisttrackId id = new PlaylisttrackId();
            id.setPlaylistId(playlist.get().getId());
            id.setTrackId(track.get().getId());
            return playlisttrackRepository.findById(id).map(existingPlaylistTrack -> {
                if (playlistTrackDTO.getPlaylistName() != null) existingPlaylistTrack.getPlaylist().setName(playlistTrackDTO.getPlaylistName());
                if (playlistTrackDTO.getTrackName() != null) existingPlaylistTrack.getTrack().setName(playlistTrackDTO.getTrackName());
                Playlisttrack updatedPlaylistTrack = playlisttrackRepository.save(existingPlaylistTrack);
                return playlistTrackDtoConverter.convertToDTO(updatedPlaylistTrack);
            });
        }
        return Optional.empty();
    }

    public boolean deletePlaylistTrack(String playlistName, String trackName) {
        Optional<Playlist> playlist = playlistRepository.findByName(playlistName);
        Optional<Track> track = trackRepository.findByName(trackName);
        if (playlist.isPresent() && track.isPresent()) {
            PlaylisttrackId id = new PlaylisttrackId();
            id.setPlaylistId(playlist.get().getId());
            id.setTrackId(track.get().getId());
            if (playlisttrackRepository.existsById(id)) {
                playlisttrackRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }

    private void setAssociations(Playlisttrack playlistTrack, PlaylistTrackDTO playlistTrackDTO) {
        Optional<Playlist> playlist = playlistRepository.findById(playlistTrackDTO.getPlaylistId());
        Optional<Track> track = trackRepository.findById(playlistTrackDTO.getTrackId());

        playlist.ifPresent(playlistTrack::setPlaylist);
        track.ifPresent(playlistTrack::setTrack);
    }
}