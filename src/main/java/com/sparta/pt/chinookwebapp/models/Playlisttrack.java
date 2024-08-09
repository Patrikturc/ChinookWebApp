package com.sparta.pt.chinookwebapp.models;

import jakarta.persistence.*;

@Entity
@Table(name = "playlisttrack", schema = "chinook")
public class Playlisttrack {
    @EmbeddedId
    private PlaylisttrackId id;

    @MapsId("playlistId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PlaylistId", nullable = false)
    private Playlist playlist;

    @MapsId("trackId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TrackId", nullable = false)
    private Track track;

    public PlaylisttrackId getId() {
        return id;
    }

    public void setId(PlaylisttrackId id) {
        this.id = id;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

}