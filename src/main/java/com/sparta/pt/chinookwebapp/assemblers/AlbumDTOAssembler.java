package com.sparta.pt.chinookwebapp.assemblers;

import com.sparta.pt.chinookwebapp.controllers.api.AlbumController;
import com.sparta.pt.chinookwebapp.controllers.api.ArtistController;
import com.sparta.pt.chinookwebapp.dtos.AlbumDTO;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class AlbumDTOAssembler extends RepresentationModelAssemblerSupport<AlbumDTO, AlbumDTO> {
    public AlbumDTOAssembler() {
        super(AlbumController.class, AlbumDTO.class);
    }

    @Override
    @NonNull
    public AlbumDTO toModel(@NonNull AlbumDTO albumDTO) {
        albumDTO.add(WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(AlbumController.class).getAlbumById(albumDTO.getId())).withSelfRel());

        if (albumDTO.getArtistName() != null) {
            albumDTO.add(WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(ArtistController.class).getArtistByName(albumDTO.getArtistName())).withRel("artist"));
        }
        return albumDTO;
    }
}
