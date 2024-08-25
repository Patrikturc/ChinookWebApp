package com.sparta.pt.chinookwebapp.dtos;

import org.springframework.hateoas.RepresentationModel;

public class PlaylistTrackDTO extends RepresentationModel<PlaylistTrackDTO> {
    private int playlistId;
    private String playlistName;
    private int trackId;
    private String trackName;

    public PlaylistTrackDTO() {
    }

    public PlaylistTrackDTO(int playlistId, String playlistName, int trackId, String trackName) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.trackId = trackId;
        this.trackName = trackName;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }
}