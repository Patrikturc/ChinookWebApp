package com.sparta.pt.chinookwebapp.converters;

import com.sparta.pt.chinookwebapp.dtos.PlaylistTrackDTO;
import com.sparta.pt.chinookwebapp.models.Playlisttrack;
import com.sparta.pt.chinookwebapp.models.PlaylisttrackId;
import org.springframework.stereotype.Component;

@Component
public class PlaylistTrackDTOConverter extends BaseDTOConverter<Playlisttrack, PlaylistTrackDTO> {

    @Override
    public PlaylistTrackDTO convertToDTO(Playlisttrack playlistTrack) {
        PlaylistTrackDTO dto = new PlaylistTrackDTO();
        dto.setPlaylistId(playlistTrack.getPlaylist().getId());
        dto.setPlaylistName(playlistTrack.getPlaylist().getName());
        dto.setTrackId(playlistTrack.getTrack().getId());
        dto.setTrackName(playlistTrack.getTrack().getName());
        return dto;
    }

    @Override
    public Playlisttrack convertToEntity(PlaylistTrackDTO playlistTrackDTO) {
        Playlisttrack playlistTrack = new Playlisttrack();
        PlaylisttrackId id = new PlaylisttrackId();

        id.setPlaylistId(playlistTrackDTO.getPlaylistId());
        id.setTrackId(playlistTrackDTO.getTrackId());
        playlistTrack.setId(id);

        return playlistTrack;
    }
}