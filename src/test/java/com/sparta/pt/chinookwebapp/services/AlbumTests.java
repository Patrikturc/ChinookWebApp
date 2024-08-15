package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.dtos.AlbumDTO;
import com.sparta.pt.chinookwebapp.models.Album;
import com.sparta.pt.chinookwebapp.models.Artist;
import com.sparta.pt.chinookwebapp.repositories.AlbumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AlbumTests {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private ArtistService artistService;

    @InjectMocks
    private AlbumService albumService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    // Happy Path Test for getAllAlbums()
    @Test
    public void getAllAlbums_ShouldReturnAllAlbumDTOs() {
        Artist artist = new Artist(1, "Artist Name");
        Album album = new Album(1, "Album Title", artist);
        when(albumRepository.findAll()).thenReturn(List.of(album));

        List<AlbumDTO> albumDTOs = albumService.getAllAlbums();

        assertThat(albumDTOs, hasSize(1));
        assertThat(albumDTOs.get(0).getTitle(), is("Album Title"));
        assertThat(albumDTOs.get(0).getArtistName(), is("Artist Name"));
    }

    // Sad Path Test for getAllAlbums() when no albums exist
    @Test
    public void getAllAlbums_ShouldReturnEmptyListWhenNoAlbumsExist() {
        when(albumRepository.findAll()).thenReturn(Collections.emptyList());

        List<AlbumDTO> albumDTOs = albumService.getAllAlbums();

        assertThat(albumDTOs, is(empty()));
    }

    // Happy Path Test for getAlbumById()
    @Test
    public void getAlbumById_ShouldReturnAlbumDTO() {
        Artist artist = new Artist(1, "Artist Name");
        Album album = new Album(1, "Album Title", artist);
        when(albumRepository.findById(1)).thenReturn(Optional.of(album));

        Optional<AlbumDTO> albumDTO = albumService.getAlbumById(1);

        assertThat(albumDTO.isPresent(), is(true));
        assertThat(albumDTO.get().getTitle(), is("Album Title"));
    }

    // Sad Path Test for getAlbumById() when album does not exist
    @Test
    public void getAlbumById_ShouldReturnEmptyWhenAlbumDoesNotExist() {
        when(albumRepository.findById(1)).thenReturn(Optional.empty());

        Optional<AlbumDTO> albumDTO = albumService.getAlbumById(1);

        assertThat(albumDTO.isPresent(), is(false));
    }

    // Happy Path Test for createAlbum()
    @Test
    public void createAlbum_ShouldCreateAlbumSuccessfully() {
        Artist artist = new Artist(1, "Artist Name");
        Album album = new Album(1, "Album Title", artist);
        when(artistService.getArtistByName("Artist Name")).thenReturn(Optional.of(artist));
        when(albumRepository.findAll()).thenReturn(Collections.singletonList(album));
        when(albumRepository.save(any(Album.class))).thenReturn(album);

        Album createdAlbum = albumService.createAlbum(new Album(), "Artist Name");

        assertThat(createdAlbum.getArtist(), is(artist));
        assertThat(createdAlbum.getTitle(), is("Album Title"));
    }

    // Sad Path Test for createAlbum() when artist does not exist
    @Test
    public void createAlbum_ShouldThrowExceptionWhenArtistNotFound() {
        when(artistService.getArtistByName("Unknown Artist")).thenReturn(Optional.empty());

        try {
            albumService.createAlbum(new Album(), "Unknown Artist");
            assert false; // This line should never be reached.
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage(), is("Artist not found: Unknown Artist"));
        }
    }

    // Happy Path Test for updateAlbum()
    @Test
    public void updateAlbum_ShouldUpdateAlbumSuccessfully() {
        Artist artist = new Artist(1, "Artist Name");
        Album album = new Album(1, "Old Title", artist);
        Album newAlbumDetails = new Album(1, "New Title", artist);
        when(albumRepository.findById(1)).thenReturn(Optional.of(album));
        when(artistService.getArtistByName("Artist Name")).thenReturn(Optional.of(artist));
        when(albumRepository.save(album)).thenReturn(album);

        Optional<Album> updatedAlbum = albumService.updateAlbum(1, newAlbumDetails, "Artist Name");

        assertThat(updatedAlbum.isPresent(), is(true));
        assertThat(updatedAlbum.get().getTitle(), is("New Title"));
    }

    // Sad Path Test for updateAlbum() when album does not exist
    @Test
    public void updateAlbum_ShouldReturnEmptyWhenAlbumDoesNotExist() {
        when(albumRepository.findById(1)).thenReturn(Optional.empty());

        Optional<Album> updatedAlbum = albumService.updateAlbum(1, new Album(), "Artist Name");

        assertThat(updatedAlbum.isPresent(), is(false));
    }

    // Happy Path Test for deleteAlbum()
    @Test
    public void deleteAlbum_ShouldDeleteAlbumSuccessfully() {
        when(albumRepository.existsById(1)).thenReturn(true);
        doNothing().when(albumRepository).deleteById(1);

        boolean isDeleted = albumService.deleteAlbum(1);

        assertThat(isDeleted, is(true));
        verify(albumRepository, times(1)).deleteById(1);
    }

    // Sad Path Test for deleteAlbum() when album does not exist
    @Test
    public void deleteAlbum_ShouldReturnFalseWhenAlbumDoesNotExist() {
        when(albumRepository.existsById(1)).thenReturn(false);

        boolean isDeleted = albumService.deleteAlbum(1);

        assertThat(isDeleted, is(false));
    }
}