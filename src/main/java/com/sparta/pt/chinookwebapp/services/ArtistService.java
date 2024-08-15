package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.models.Artist;
import com.sparta.pt.chinookwebapp.repositories.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;

    @Autowired
    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }

    public Optional<Artist> getArtistById(Integer id) {
        return artistRepository.findById(id);
    }

    public Optional<Artist> getArtistByName(String artistName) {
        return artistRepository.findAll().stream()
                .filter(artist -> artist.getName().equalsIgnoreCase(artistName))
                .findFirst();
    }

    public Artist createArtist(Artist artist) {
        List<Artist> allArtists = artistRepository.findAll();

        int maxId = allArtists.stream()
                .max(Comparator.comparingInt(Artist::getId))
                .map(Artist::getId)
                .orElse(0);
        artist.setId(maxId + 1);

        return artistRepository.save(artist);
    }

    public Artist upsertArtist(Integer id, Artist artistDetails) {
        Artist artist = artistRepository.findById(id).orElse(new Artist());
        artist.setId(id);
        artist.setName(artistDetails.getName());

        return artistRepository.save(artist);
    }

    public Optional<Artist> patchArtist(Integer id, Artist artistDetails) {
        return artistRepository.findById(id)
                .map(existingArtist -> {
                    if (artistDetails.getName() != null) {
                        existingArtist.setName(artistDetails.getName());  // Update only non-null fields
                    }
                    return artistRepository.save(existingArtist);
                });
    }

    public boolean deleteArtist(Integer id) {
        if (artistRepository.existsById(id)) {
            artistRepository.deleteById(id);
            return true;
        }
        return false;
    }
}