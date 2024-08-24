package com.sparta.pt.chinookwebapp.dtos;

import org.springframework.hateoas.RepresentationModel;

public class ArtistDTO extends RepresentationModel<ArtistDTO> {
    private Integer id;
    private String name;

    public ArtistDTO() {
    }

    public ArtistDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}