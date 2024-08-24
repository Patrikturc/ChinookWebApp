package com.sparta.pt.chinookwebapp.converters;

import com.sparta.pt.chinookwebapp.dtos.ArtistDTO;
import com.sparta.pt.chinookwebapp.models.Artist;
import org.springframework.stereotype.Component;

@Component
public class ArtistDTOConverter extends BaseDTOConverter<Artist, ArtistDTO> {
    public ArtistDTO convertToDTO(Artist artist) {
        return new ArtistDTO(artist.getId(), artist.getName());
    }

    public Artist convertToEntity(ArtistDTO artistDTO) {
        Artist artist = new Artist();
        artist.setId(artistDTO.getId());
        artist.setName(artistDTO.getName());
        return artist;
    }
}
