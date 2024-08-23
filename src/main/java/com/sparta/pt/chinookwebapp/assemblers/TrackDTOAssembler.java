package com.sparta.pt.chinookwebapp.assemblers;

import com.sparta.pt.chinookwebapp.controllers.api.AlbumController;
import com.sparta.pt.chinookwebapp.controllers.api.MediatypeController;
import com.sparta.pt.chinookwebapp.controllers.api.TrackController;
import com.sparta.pt.chinookwebapp.dtos.TrackDTO;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class TrackDTOAssembler extends RepresentationModelAssemblerSupport<TrackDTO, TrackDTO> {

    public TrackDTOAssembler() {
        super(TrackController.class, TrackDTO.class);
    }

    @Override
    @NonNull
    public TrackDTO toModel(@NonNull TrackDTO trackDTO) {
        trackDTO.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(TrackController.class).getTrackById(trackDTO.getId())).withSelfRel());

        if (trackDTO.getAlbumTitle() != null) {
            trackDTO.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(AlbumController.class).getAlbumByTitle(trackDTO.getAlbumTitle())).withRel("album"));
        }

        if (trackDTO.getMediaTypeName() != null) {
            trackDTO.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(MediatypeController.class).getMediatypeByName(trackDTO.getMediaTypeName())).withRel("mediaType"));
        }
        return trackDTO;
    }
}