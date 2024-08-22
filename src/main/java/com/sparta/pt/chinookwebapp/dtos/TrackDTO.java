package com.sparta.pt.chinookwebapp.dtos;

import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

public class TrackDTO extends RepresentationModel<TrackDTO> {
    private Integer id;
    private String name;
    private String composer;
    private Integer milliseconds;
    private Integer bytes;
    private BigDecimal unitPrice;
    private String albumTitle;
    private String mediaTypeName;
    private String genreName;

    public TrackDTO() {
    }

    public TrackDTO(Integer id, String name, String composer, Integer milliseconds, Integer bytes, BigDecimal unitPrice, String albumTitle, String mediaTypeName, String genreName) {
        this.id = id;
        this.name = name;
        this.composer = composer;
        this.milliseconds = milliseconds;
        this.bytes = bytes;
        this.unitPrice = unitPrice;
        this.albumTitle = albumTitle;
        this.mediaTypeName = mediaTypeName;
        this.genreName = genreName;
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

    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public Integer getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(Integer milliseconds) {
        this.milliseconds = milliseconds;
    }

    public Integer getBytes() {
        return bytes;
    }

    public void setBytes(Integer bytes) {
        this.bytes = bytes;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public void setAlbumTitle(String albumTitle) {
        this.albumTitle = albumTitle;
    }

    public String getMediaTypeName() {
        return mediaTypeName;
    }

    public void setMediaTypeName(String mediaTypeName) {
        this.mediaTypeName = mediaTypeName;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }
}