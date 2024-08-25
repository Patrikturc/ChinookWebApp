package com.sparta.pt.chinookwebapp.converters;

import com.sparta.pt.chinookwebapp.dtos.AlbumDTO;
import com.sparta.pt.chinookwebapp.models.Album;
import com.sparta.pt.chinookwebapp.models.Artist;
import com.sparta.pt.chinookwebapp.repositories.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AlbumDTOConverter extends BaseDTOConverter<Album, AlbumDTO> {

    private final ArtistRepository artistRepository;

    @Autowired
    public AlbumDTOConverter(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
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
        setArtistByName(album, albumDTO.getArtistName());
        return album;
    }

    public void setArtistByName(Album album, String artistName) {
        if (artistName != null && !artistName.isEmpty()) {
            Optional<Artist> artistOptional = artistRepository.getArtistByName(artistName);
            artistOptional.ifPresent(album::setArtist);
        }
    }
}