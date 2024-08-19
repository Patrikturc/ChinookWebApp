package com.sparta.pt.chinookwebapp.dtos;

import com.sparta.pt.chinookwebapp.controllers.api.AlbumController;
import com.sparta.pt.chinookwebapp.controllers.api.ArtistController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

public class AlbumDTO {
    private Integer id;
    private String title;
    private String artistName;  // Change to String for direct artist name

    public AlbumDTO() {
    }

    public AlbumDTO(Integer id, String title, String artistName) {
        this.id = id;
        this.title = title;
        this.artistName = artistName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
}