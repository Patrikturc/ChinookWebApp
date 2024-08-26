package com.sparta.pt.chinookwebapp.assemblers;

import com.sparta.pt.chinookwebapp.controllers.api.PlaylistController;
import com.sparta.pt.chinookwebapp.dtos.PlaylistDTO;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.lang.NonNull;

public class PlaylistDTOAssembler extends RepresentationModelAssemblerSupport<PlaylistDTO, PlaylistDTO> {

        public PlaylistDTOAssembler() {
            super(PlaylistDTO.class, PlaylistDTO.class);
        }

        @Override
        @NonNull
        public PlaylistDTO toModel(PlaylistDTO playlistDTO) {
            playlistDTO.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(PlaylistController.class).getPlaylistById(playlistDTO.getId())).withSelfRel());
            return playlistDTO;
        }
}
