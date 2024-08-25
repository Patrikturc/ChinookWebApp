package com.sparta.pt.chinookwebapp.assemblers;

import com.sparta.pt.chinookwebapp.controllers.api.PlaylistTrackController;
import com.sparta.pt.chinookwebapp.controllers.api.PlaylistController;
import com.sparta.pt.chinookwebapp.controllers.api.TrackController;
import com.sparta.pt.chinookwebapp.dtos.PlaylistTrackDTO;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class PlaylistTrackDTOAssembler extends RepresentationModelAssemblerSupport<PlaylistTrackDTO, PlaylistTrackDTO> {

    public PlaylistTrackDTOAssembler() {
        super(PlaylistTrackController.class, PlaylistTrackDTO.class);
    }

    @Override
    @NonNull
    public PlaylistTrackDTO toModel(@NonNull PlaylistTrackDTO playlistTrackDTO) {
        playlistTrackDTO.add(WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(PlaylistTrackController.class)
                                .getPlaylistTrackById(playlistTrackDTO.getPlaylistName(), playlistTrackDTO.getTrackName()))
                .withSelfRel());

        if (playlistTrackDTO.getPlaylistName() != null) {
            playlistTrackDTO.add(WebMvcLinkBuilder.linkTo(
                            WebMvcLinkBuilder.methodOn(PlaylistController.class)
                                    .getPlaylistByName(playlistTrackDTO.getPlaylistName()))
                    .withRel("playlist"));
        }

        if (playlistTrackDTO.getTrackName() != null) {
            playlistTrackDTO.add(WebMvcLinkBuilder.linkTo(
                            WebMvcLinkBuilder.methodOn(TrackController.class)
                                    .getTrackById(playlistTrackDTO.getTrackId()))
                    .withRel("track"));
        }

        return playlistTrackDTO;
    }
}