package com.sparta.pt.chinookwebapp.converters;

import com.sparta.pt.chinookwebapp.dtos.AlbumDTO;
import com.sparta.pt.chinookwebapp.dtos.ArtistDTO;
import com.sparta.pt.chinookwebapp.models.Album;
import com.sparta.pt.chinookwebapp.models.Artist;
import com.sparta.pt.chinookwebapp.repositories.ArtistRepository;
import com.sparta.pt.chinookwebapp.services.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AlbumDTOConverter extends BaseDTOConverter<Album, AlbumDTO> {

    private final ArtistService artistService;
    private final ArtistDTOConverter artistDTOConverter;

    public AlbumDTOConverter(ArtistService artistService, ArtistDTOConverter artistDTOConverter) {
        this.artistService = artistService;
        this.artistDTOConverter = artistDTOConverter;
    }

    @Override
    public AlbumDTO convertToDTO(Album album) {
        return new AlbumDTO(
                album.getId(),
                album.getTitle(),
                album.getArtist() != null ? album.getArtist().getName() : null);
    }

    @Override
    public Album convertToEntity(AlbumDTO albumDTO) {
        Album album = new Album();
        album.setId(albumDTO.getId());
        album.setTitle(albumDTO.getTitle());
        return album;
    }
}