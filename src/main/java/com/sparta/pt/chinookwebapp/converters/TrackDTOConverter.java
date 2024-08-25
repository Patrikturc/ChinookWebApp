package com.sparta.pt.chinookwebapp.converters;

import com.sparta.pt.chinookwebapp.dtos.TrackDTO;
import com.sparta.pt.chinookwebapp.models.Track;
import org.springframework.stereotype.Component;

@Component
public class TrackDTOConverter extends BaseDTOConverter<Track, TrackDTO> {

    @Override
    public TrackDTO convertToDTO(Track track) {
        return new TrackDTO(track.getId(), track.getName(), track.getComposer(), track.getMilliseconds(),
                track.getBytes(), track.getUnitPrice(),
                track.getAlbum() != null ? track.getAlbum().getTitle() : null,
                track.getMediaType() != null ? track.getMediaType().getName() : null,
                track.getGenre() != null ? track.getGenre().getName() : null);
    }

    @Override
    public Track convertToEntity(TrackDTO trackDTO) {
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