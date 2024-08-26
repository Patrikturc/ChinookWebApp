package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.converters.PlaylistDTOConverter;
import com.sparta.pt.chinookwebapp.dtos.PlaylistDTO;
import com.sparta.pt.chinookwebapp.models.Playlist;
import com.sparta.pt.chinookwebapp.repositories.PlaylistRepository;
import com.sparta.pt.chinookwebapp.utils.IdManagementUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final IdManagementUtils idManagementUtils;
    private final PlaylistDTOConverter playlistDTOConverter;

    public PlaylistService(PlaylistRepository playlistRepository, IdManagementUtils idManagementUtils, PlaylistDTOConverter playlistDTOConverter) {
        this.playlistRepository = playlistRepository;
        this.idManagementUtils = idManagementUtils;
        this.playlistDTOConverter = playlistDTOConverter;
    }

    public Page<PlaylistDTO> getAllPlaylists(int page, int size) {
        Page<Playlist> playlists = playlistRepository.findAll(PageRequest.of(page, size));
        return playlists.map(playlistDTOConverter::convertToDTO);
    }

    public Optional<PlaylistDTO> getPlaylistById(int id) {
        return playlistRepository.findById(id).map(playlistDTOConverter::convertToDTO);
    }

    public Optional<PlaylistDTO> getPlaylistByName(String name) {
        return playlistRepository.findByName(name).map(playlistDTOConverter::convertToDTO);
    }

    public PlaylistDTO createPlaylist(PlaylistDTO playlistDTO) {
        Playlist playlist = playlistDTOConverter.convertToEntity(playlistDTO);

        List<Playlist> allPlaylists = playlistRepository.findAll();
        int newId = idManagementUtils.generateId(allPlaylists, Playlist::getId);
        playlist.setId(newId);

        Playlist savedPlaylist = playlistRepository.save(playlist);
        return playlistDTOConverter.convertToDTO(savedPlaylist);
    }

    public PlaylistDTO upsertPlaylist(int id, PlaylistDTO playlistDTO) {
        Playlist playlist = playlistDTOConverter.convertToEntity(playlistDTO);
        playlist.setId(id);

        if (!playlistRepository.existsById(id)) {
            Playlist savedPlaylist = playlistRepository.save(playlist);
            return playlistDTOConverter.convertToDTO(savedPlaylist);
        }

        updatePlaylist(id, playlistDTO);
        return playlistDTOConverter.convertToDTO(playlist);
    }

    public Optional<PlaylistDTO> updatePlaylist(int id, PlaylistDTO playlistDTO) {
        return playlistRepository.findById(id).map(existingPlaylist -> {
            if (playlistDTO.getName() != null) existingPlaylist.setName(playlistDTO.getName());
            Playlist updatedPlaylist = playlistRepository.save(existingPlaylist);
            return playlistDTOConverter.convertToDTO(updatedPlaylist);
        });
    }

    public boolean deletePlaylist(int id) {
        if (playlistRepository.existsById(id)) {
            playlistRepository.deleteById(id);
            return true;
        }
        return false;
    }
}