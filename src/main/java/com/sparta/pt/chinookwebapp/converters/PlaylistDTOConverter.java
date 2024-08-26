package com.sparta.pt.chinookwebapp.converters;

import com.sparta.pt.chinookwebapp.dtos.PlaylistDTO;
import com.sparta.pt.chinookwebapp.models.Playlist;
import org.springframework.stereotype.Component;

@Component
public class PlaylistDTOConverter extends BaseDTOConverter<Playlist, PlaylistDTO> {

    @Override
    public PlaylistDTO convertToDTO(Playlist playlist) {
        return new PlaylistDTO(
                playlist.getId(),
                playlist.getName());
    }

    @Override
    public Playlist convertToEntity(PlaylistDTO playlistDTO) {
        Playlist playlist = new Playlist();
        playlist.setId(playlistDTO.getId());
        playlist.setName(playlistDTO.getName());
        return playlist;
    }
}
