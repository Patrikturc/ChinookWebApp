package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.dtos.PlaylistTrackDTO;
import com.sparta.pt.chinookwebapp.models.Playlist;
import com.sparta.pt.chinookwebapp.models.Playlisttrack;
import com.sparta.pt.chinookwebapp.models.PlaylisttrackId;
import com.sparta.pt.chinookwebapp.models.Track;
import com.sparta.pt.chinookwebapp.repositories.PlaylistRepository;
import com.sparta.pt.chinookwebapp.repositories.PlaylisttrackRepository;
import com.sparta.pt.chinookwebapp.repositories.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlaylistTrackService {

    private final PlaylisttrackRepository playlisttrackRepository;
    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;

    public PlaylistTrackService(PlaylisttrackRepository playlisttrackRepository, PlaylistRepository playlistRepository, TrackRepository trackRepository) {
        this.playlisttrackRepository = playlisttrackRepository;
        this.playlistRepository = playlistRepository;
        this.trackRepository = trackRepository;
    }

    public Page<PlaylistTrackDTO> getAllPlaylistTracks(int page, int size) {
        Page<Playlisttrack> playlistTracks = playlisttrackRepository.findAll(PageRequest.of(page, size));
        return playlistTracks.map(this::convertToDTO);
    }

    public Optional<PlaylistTrackDTO> getPlaylistTrackById(String playlistName, String trackName) {
        Optional<Playlist> playlist = playlistRepository.findByName(playlistName);
        Optional<Track> track = trackRepository.findByName(trackName);
        if (playlist.isPresent() && track.isPresent()) {
            PlaylisttrackId id = new PlaylisttrackId();
            id.setPlaylistId(playlist.get().getId());
            id.setTrackId(track.get().getId());
            return playlisttrackRepository.findById(id).map(this::convertToDTO);
        }
        return Optional.empty();
    }

    public Page<PlaylistTrackDTO> getTracksByPlaylistName(String playlistName, int page, int size) {
        Optional<Playlist> playlist = playlistRepository.findByName(playlistName);
        Pageable pageable = PageRequest.of(page, size);
        Page<Playlisttrack> playlistTracks = playlisttrackRepository.findByPlaylistId(playlist.get().getId(), pageable);
        return playlistTracks.map(this::convertToDTO);
    }

    public PlaylistTrackDTO createPlaylistTrack(PlaylistTrackDTO playlistTrackDTO) {
        Playlisttrack playlistTrack = convertToEntity(playlistTrackDTO);
        Playlisttrack savedPlaylistTrack = playlisttrackRepository.save(playlistTrack);
        return convertToDTO(savedPlaylistTrack);
    }

    public PlaylistTrackDTO upsertPlaylistTrack(String playlistName, String trackName, PlaylistTrackDTO playlistTrackDTO) {
        Optional<Playlist> playlist = playlistRepository.findByName(playlistName);
        Optional<Track> track = trackRepository.findByName(trackName);
        if (playlist.isPresent() && track.isPresent()) {
            PlaylisttrackId id = new PlaylisttrackId();
            id.setPlaylistId(playlist.get().getId());
            id.setTrackId(track.get().getId());
            Playlisttrack playlistTrack = convertToEntity(playlistTrackDTO);
            playlistTrack.setId(id);

            if (!playlisttrackRepository.existsById(id)) {
                Playlisttrack savedPlaylistTrack = playlisttrackRepository.save(playlistTrack);
                return convertToDTO(savedPlaylistTrack);
            }

            updatePlaylistTrack(playlistName, trackName, playlistTrackDTO);
            return convertToDTO(playlistTrack);
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
                return convertToDTO(updatedPlaylistTrack);
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

    private PlaylistTrackDTO convertToDTO(Playlisttrack playlistTrack) {
        PlaylistTrackDTO dto = new PlaylistTrackDTO();
        dto.setPlaylistId(playlistTrack.getPlaylist().getId());
        dto.setPlaylistName(playlistTrack.getPlaylist().getName());
        dto.setTrackId(playlistTrack.getTrack().getId());
        dto.setTrackName(playlistTrack.getTrack().getName());
        return dto;
    }

    private Playlisttrack convertToEntity(PlaylistTrackDTO playlistTrackDTO) {
        Playlisttrack playlistTrack = new Playlisttrack();
        PlaylisttrackId id = new PlaylisttrackId();

        Optional<Playlist> playlist = playlistRepository.findByName(playlistTrackDTO.getPlaylistName());
        Optional<Track> track = trackRepository.findByName(playlistTrackDTO.getTrackName());

        if (playlist.isPresent() && track.isPresent()) {
            id.setPlaylistId(playlist.get().getId());
            id.setTrackId(track.get().getId());
            playlistTrack.setId(id);
            playlistTrack.setPlaylist(playlist.get());
            playlistTrack.setTrack(track.get());
        }

        return playlistTrack;
    }
}