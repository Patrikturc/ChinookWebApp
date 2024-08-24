package com.sparta.pt.chinookwebapp.assemblers;

import com.sparta.pt.chinookwebapp.controllers.api.ArtistController;
import com.sparta.pt.chinookwebapp.dtos.ArtistDTO;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ArtistDTOAssembler extends RepresentationModelAssemblerSupport<ArtistDTO, ArtistDTO> {

        public ArtistDTOAssembler() {
            super(ArtistDTO.class, ArtistDTO.class);
        }

        @Override
        @NonNull
        public ArtistDTO toModel(ArtistDTO artistDTO) {
            artistDTO.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(ArtistController.class).getArtistById(artistDTO.getId())).withSelfRel());
            return artistDTO;
        }

}
