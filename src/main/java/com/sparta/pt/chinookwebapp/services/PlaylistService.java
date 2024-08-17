package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.dtos.PlaylistDTO;
import com.sparta.pt.chinookwebapp.models.Playlist;
import com.sparta.pt.chinookwebapp.repositories.PlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class PlaylistService {

    @Autowired
    private PlaylistRepository playlistRepository;

    public Page<PlaylistDTO> getAllPlaylists(int page, int size) {
        Page<Playlist> playlists = playlistRepository.findAll(PageRequest.of(page, size));
        return playlists.map(this::convertToDTO);
    }

    public Optional<PlaylistDTO> getPlaylistById(int id) {
        return playlistRepository.findById(id).map(this::convertToDTO);
    }

    public PlaylistDTO createPlaylist(PlaylistDTO playlistDTO) {
        Playlist playlist = convertToEntity(playlistDTO);
        List<Playlist> allPlaylists = playlistRepository.findAll();

        int maxId = allPlaylists.stream()
                .max(Comparator.comparingInt(Playlist::getId))
                .map(Playlist::getId)
                .orElse(0);
        playlist.setId(maxId + 1);

        Playlist savedPlaylist = playlistRepository.save(playlist);
        return convertToDTO(savedPlaylist);
    }

    public PlaylistDTO upsertPlaylist(int id, PlaylistDTO playlistDTO) {
        Playlist playlist = convertToEntity(playlistDTO);
        playlist.setId(id);

        if (!playlistRepository.existsById(id)) {
            Playlist savedPlaylist = playlistRepository.save(playlist);
            return convertToDTO(savedPlaylist);
        }

        updatePlaylist(id, playlistDTO);
        return convertToDTO(playlist);
    }

    public Optional<PlaylistDTO> updatePlaylist(int id, PlaylistDTO playlistDTO) {
        return playlistRepository.findById(id).map(existingPlaylist -> {
            if (playlistDTO.getName() != null) existingPlaylist.setName(playlistDTO.getName());
            Playlist updatedPlaylist = playlistRepository.save(existingPlaylist);
            return convertToDTO(updatedPlaylist);
        });
    }

    public boolean deletePlaylist(int id) {
        if (playlistRepository.existsById(id)) {
            playlistRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private PlaylistDTO convertToDTO(Playlist playlist) {
        PlaylistDTO dto = new PlaylistDTO();
        dto.setId(playlist.getId());
        dto.setName(playlist.getName());
        return dto;
    }

    private Playlist convertToEntity(PlaylistDTO playlistDTO) {
        Playlist playlist = new Playlist();
        playlist.setId(playlistDTO.getId());
        playlist.setName(playlistDTO.getName());
        return playlist;
    }
}