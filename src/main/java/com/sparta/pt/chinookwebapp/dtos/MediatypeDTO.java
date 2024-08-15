package com.sparta.pt.chinookwebapp.dtos;

public class MediatypeDTO {

    private Integer id;
    private String name;

    public MediatypeDTO() {
    }

    public MediatypeDTO(Integer id, String name) {
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