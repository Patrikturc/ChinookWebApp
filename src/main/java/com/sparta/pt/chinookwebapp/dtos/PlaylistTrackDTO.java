package com.sparta.pt.chinookwebapp.dtos;

public class PlaylistTrackDTO {
    private Integer playlistId;
    private Integer trackId;

    public PlaylistTrackDTO() {
    }

    public PlaylistTrackDTO(Integer playlistId, Integer trackId) {
        this.playlistId = playlistId;
        this.trackId = trackId;
    }

    public Integer getPlaylistId() {
        return playlistId;
    }

    public Integer getTrackId() {
        return trackId;
    }
}
