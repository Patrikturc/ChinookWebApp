package com.sparta.pt.chinookwebapp.dtos;

public class PlaylistDTO {
    private Integer id;
    private String name;

    public PlaylistDTO() {
    }

    public PlaylistDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
