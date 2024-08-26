package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.converters.ArtistDTOConverter;
import com.sparta.pt.chinookwebapp.dtos.ArtistDTO;
import com.sparta.pt.chinookwebapp.models.Artist;
import com.sparta.pt.chinookwebapp.repositories.ArtistRepository;
import com.sparta.pt.chinookwebapp.utils.IdManagementUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final ArtistDTOConverter artistDTOConverter;
    private final IdManagementUtils idManagementUtils;

    @Autowired
    public ArtistService(ArtistRepository artistRepository, ArtistDTOConverter artistDTOConverter, IdManagementUtils idManagementUtils) {
        this.artistRepository = artistRepository;
        this.artistDTOConverter = artistDTOConverter;
        this.idManagementUtils = idManagementUtils;
    }

    public Page<ArtistDTO> getAllArtists(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return artistRepository.findAll(pageable).map(artistDTOConverter::convertToDTO);
    }

    public Optional<ArtistDTO> getArtistById(Integer id) {
        return artistRepository.findById(id).map(artistDTOConverter::convertToDTO);
    }

    public Optional<ArtistDTO> getArtistByName(String artistName) {
        return artistRepository.findByName(artistName).map(artistDTOConverter::convertToDTO);
    }

    public ArtistDTO createArtist(ArtistDTO artistDTO) {
        Artist artist = artistDTOConverter.convertToEntity(artistDTO);

        List<Artist> allArtists = artistRepository.findAll();
        int newId = idManagementUtils.generateId(allArtists, Artist::getId);
        artist.setId(newId);

        Artist savedArtist = artistRepository.save(artist);
        return artistDTOConverter.convertToDTO(savedArtist);
    }

    public ArtistDTO upsertArtist(Integer id, ArtistDTO artistDetails) {
        Artist artist = artistRepository.findById(id).orElse(new Artist());
        artist.setId(id);
        artist.setName(artistDetails.getName());

        Artist savedArtist = artistRepository.save(artist);
        return artistDTOConverter.convertToDTO(savedArtist);
    }

    public Optional<ArtistDTO> patchArtist(Integer id, Artist artistDetails) {
        return artistRepository.findById(id).map(existingArtist -> {
                if (artistDetails.getName() != null) {
                    existingArtist.setName(artistDetails.getName());
                }
                Artist savedArtist = artistRepository.save(existingArtist);
                return artistDTOConverter.convertToDTO(savedArtist);
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