package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.controllers.api.AlbumController;
import com.sparta.pt.chinookwebapp.controllers.api.GenreController;
import com.sparta.pt.chinookwebapp.controllers.api.TrackController;
import com.sparta.pt.chinookwebapp.dtos.TrackDTO;
import com.sparta.pt.chinookwebapp.models.Track;
import com.sparta.pt.chinookwebapp.repositories.MediatypeRepository;
import com.sparta.pt.chinookwebapp.repositories.TrackRepository;
import com.sparta.pt.chinookwebapp.utils.HateoasUtils;
import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilderFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class TrackService extends BaseService<Track, TrackDTO, TrackRepository> {

    private final TrackRepository trackRepository;
    private final MediatypeRepository mediaTypeRepository;

    @Autowired
    public TrackService(TrackRepository trackRepository, HateoasUtils<TrackDTO> hateoasUtils, WebMvcLinkBuilderFactory linkBuilderFactory, MediatypeRepository mediaTypeRepository) {
        super(trackRepository, hateoasUtils, linkBuilderFactory);
        this.mediaTypeRepository = mediaTypeRepository;
        this.trackRepository = trackRepository;
    }

    public ResponseEntity<PagedModel<EntityModel<TrackDTO>>> getAllTracks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Track> tracks = trackRepository.findAll(pageable);
        return getPagedModelResponseEntity(tracks);
    }

    public ResponseEntity<EntityModel<TrackDTO>> getTrackById(int id) {
        Track track = trackRepository.findById(id)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("Track not found"));
        TrackDTO trackDTO = convertToDTO(track);
        EntityModel<TrackDTO> entityModel = EntityModel.of(trackDTO);
        WebMvcLinkBuilder selfLinkBuilder = WebMvcLinkBuilder.linkTo(TrackController.class).slash(trackDTO.getId());
        addCustomLinks(trackDTO);
        entityModel.add(selfLinkBuilder.withSelfRel());
        return ResponseEntity.ok(entityModel);
    }

    public ResponseEntity<EntityModel<TrackDTO>> createTrack(TrackDTO trackDTO) {
        Track track = convertToEntity(trackDTO);
        return create(track, this::convertToDTO, TrackController.class, TrackDTO::getId);
    }

    public ResponseEntity<EntityModel<TrackDTO>> updateTrack(int id, TrackDTO trackDTO) {
        Track trackDetails = convertToEntity(trackDTO);
        return update(id, trackDetails, this::convertToDTO, TrackController.class, TrackDTO::getId);
    }

    public ResponseEntity<Void> deleteTrack(int id) {
        return delete(id);
    }

    public ResponseEntity<PagedModel<EntityModel<TrackDTO>>> getTracksByGenreName(String genreName, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Track> tracks = trackRepository.findByGenreNameContainingIgnoreCase(genreName, pageable);
        return getPagedModelResponseEntity(tracks);
    }

    public ResponseEntity<PagedModel<EntityModel<TrackDTO>>> getTracksByAlbumTitle(String albumTitle, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Track> tracks = trackRepository.findByAlbumTitleContainingIgnoreCase(albumTitle, pageable);
        return getPagedModelResponseEntity(tracks);
    }

    private TrackDTO convertToDTO(Track track) {
        TrackDTO dto = new TrackDTO();
        dto.setId(track.getId());
        dto.setName(track.getName());
        dto.setComposer(track.getComposer());
        dto.setMilliseconds(track.getMilliseconds());
        dto.setBytes(track.getBytes());
        dto.setUnitPrice(track.getUnitPrice());
        dto.setAlbumTitle(track.getAlbum() != null ? track.getAlbum().getTitle() : null);
        dto.setMediaTypeName(track.getMediaType() != null ? track.getMediaType().getName() : null);
        dto.setGenreName(track.getGenre() != null ? track.getGenre().getName() : null);
        return dto;
    }

    private Track convertToEntity(TrackDTO trackDTO) {
        Track track = new Track();
        track.setId(trackDTO.getId());
        track.setName(trackDTO.getName());
        track.setComposer(trackDTO.getComposer());
        track.setMilliseconds(trackDTO.getMilliseconds());
        track.setBytes(trackDTO.getBytes());
        track.setUnitPrice(trackDTO.getUnitPrice());
        if (trackDTO.getMediaTypeName() != null) {
            track.setMediaType(mediaTypeRepository.findByName(trackDTO.getMediaTypeName()).orElse(null));
        }
        return track;
    }

    @Override
    protected void updateEntity(Track existingTrack, Track trackDetails) {
        if (trackDetails.getName() != null) existingTrack.setName(trackDetails.getName());
        if (trackDetails.getComposer() != null) existingTrack.setComposer(trackDetails.getComposer());
        if (trackDetails.getMilliseconds() != null) existingTrack.setMilliseconds(trackDetails.getMilliseconds());
        if (trackDetails.getBytes() != null) existingTrack.setBytes(trackDetails.getBytes());
        if (trackDetails.getUnitPrice() != null) existingTrack.setUnitPrice(trackDetails.getUnitPrice());
    }

    @Override
    protected void updateEntityPartial(Track existingEntity, TrackDTO dtoDetails) {
        if (dtoDetails.getName() != null) existingEntity.setName(dtoDetails.getName());
        if (dtoDetails.getComposer() != null) existingEntity.setComposer(dtoDetails.getComposer());
        if (dtoDetails.getMilliseconds() != null) existingEntity.setMilliseconds(dtoDetails.getMilliseconds());
        if (dtoDetails.getBytes() != null) existingEntity.setBytes(dtoDetails.getBytes());
        if (dtoDetails.getUnitPrice() != null) existingEntity.setUnitPrice(dtoDetails.getUnitPrice());
        if (dtoDetails.getMediaTypeName() != null) {
            existingEntity.setMediaType(mediaTypeRepository.findByName(dtoDetails.getMediaTypeName()).orElse(null));
        }
    }

    private ResponseEntity<PagedModel<EntityModel<TrackDTO>>> getPagedModelResponseEntity(Page<Track> tracks) {
        Page<TrackDTO> trackDTOs = tracks.map(this::convertToDTO);
        PagedModel<EntityModel<TrackDTO>> pagedModel = hateoasUtils.createPagedResponse(trackDTOs, TrackController.class, TrackDTO::getId).getBody();
        if (pagedModel != null) {
            pagedModel.getContent().forEach(entityModel -> addCustomLinks(entityModel.getContent()));
        }
        return ResponseEntity.ok(pagedModel);
    }

    private void addCustomLinks(TrackDTO trackDTO) {
        WebMvcLinkBuilder albumLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AlbumController.class).getAlbumByTitle(trackDTO.getAlbumTitle()));
        WebMvcLinkBuilder genreLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GenreController.class).getGenreByName(trackDTO.getGenreName()));
        trackDTO.add(albumLink.withRel("album"));
        trackDTO.add(genreLink.withRel("genre"));
    }
}
