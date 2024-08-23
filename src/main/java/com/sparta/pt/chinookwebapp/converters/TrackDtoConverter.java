package com.sparta.pt.chinookwebapp.converters;

import com.sparta.pt.chinookwebapp.dtos.TrackDTO;
import com.sparta.pt.chinookwebapp.models.Album;
import com.sparta.pt.chinookwebapp.models.Genre;
import com.sparta.pt.chinookwebapp.models.Mediatype;
import com.sparta.pt.chinookwebapp.models.Track;
import com.sparta.pt.chinookwebapp.repositories.AlbumRepository;
import com.sparta.pt.chinookwebapp.repositories.GenreRepository;
import com.sparta.pt.chinookwebapp.repositories.MediatypeRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TrackDtoConverter extends BaseDtoConverter<Track, TrackDTO> {

    private final AlbumRepository albumRepository;
    private final MediatypeRepository mediatypeRepository;
    private final GenreRepository genreRepository;

    public TrackDtoConverter(AlbumRepository albumRepository, MediatypeRepository mediatypeRepository, GenreRepository genreRepository) {
        this.albumRepository = albumRepository;
        this.mediatypeRepository = mediatypeRepository;
        this.genreRepository = genreRepository;
    }

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
        setAlbumByTitle(track, trackDTO.getAlbumTitle());
        setMediaTypeByName(track, trackDTO.getMediaTypeName());
        setGenreByName(track, trackDTO.getGenreName());
        return track;
    }

    public void setAlbumByTitle(Track track, String albumTitle) {
        if (albumTitle != null && !albumTitle.isEmpty()) {
            Optional<Album> album = albumRepository.findByTitle(albumTitle);
            album.ifPresent(track::setAlbum);
        }
    }

    public void setMediaTypeByName(Track track, String mediaTypeName) {
        if (mediaTypeName != null && !mediaTypeName.isEmpty()) {
            Optional<Mediatype> mediaType = mediatypeRepository.getMediatypeByName(mediaTypeName);
            mediaType.ifPresent(track::setMediaType);
        }
    }

    public void setGenreByName(Track track, String genreName) {
        if (genreName != null && !genreName.isEmpty()) {
            Optional<Genre> genre = genreRepository.getGenreByName(genreName);
            genre.ifPresent(track::setGenre);
        }
    }
}