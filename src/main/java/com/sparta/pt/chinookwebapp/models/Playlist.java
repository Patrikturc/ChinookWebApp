package com.sparta.pt.chinookwebapp.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "playlist", schema = "chinook")
public class Playlist {
    @Id
    @Column(name = "PlaylistId", nullable = false)
    private Integer id;

    @Size(max = 120)
    @Column(name = "Name", length = 120)
    private String name;

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