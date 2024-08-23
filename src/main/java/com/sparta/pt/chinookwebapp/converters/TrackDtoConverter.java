package com.sparta.pt.chinookwebapp.converters;

import com.sparta.pt.chinookwebapp.dtos.TrackDTO;
import com.sparta.pt.chinookwebapp.models.Track;
import com.sparta.pt.chinookwebapp.services.AlbumService;
import com.sparta.pt.chinookwebapp.services.GenreService;
import com.sparta.pt.chinookwebapp.services.MediatypeService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TrackDtoConverter extends BaseDtoConverter<Track, TrackDTO> {

    private final AlbumService albumService;
    private final MediatypeService mediatypeService;
    private final GenreService genreService;
    private final AlbumDtoConverter albumDtoConverter;
    private final GenreDtoConverter genreDtoConverter;
    private final MediatypeDtoConverter mediatypeDtoConverter;

    public TrackDtoConverter(AlbumService albumService, MediatypeService mediatypeService, GenreService genreService, AlbumDtoConverter albumDtoConverter, GenreDtoConverter genreDtoConverter, MediatypeDtoConverter mediatypeDtoConverter) {
        this.albumService = albumService;
        this.mediatypeService = mediatypeService;
        this.genreService = genreService;
        this.albumDtoConverter = albumDtoConverter;
        this.genreDtoConverter = genreDtoConverter;
        this.mediatypeDtoConverter = mediatypeDtoConverter;
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
            albumService.getAlbumByTitle(albumTitle)
                    .map(albumDtoConverter::convertToEntity)
                    .ifPresent(track::setAlbum);
        }
    }

    public void setMediaTypeByName(Track track, String mediaTypeName) {
        if (mediaTypeName != null && !mediaTypeName.isEmpty()) {
            mediatypeService.getMediatypeByName(mediaTypeName)
                    .map(mediatypeDtoConverter::convertToEntity)
                    .ifPresent(track::setMediaType);
        }
    }

    public void setGenreByName(Track track, String genreName) {
        if (genreName != null && !genreName.isEmpty()) {
            genreService.getGenreByName(genreName)
                    .map(genreDtoConverter::convertToEntity)
                    .ifPresent(track::setGenre);
        }
    }
}