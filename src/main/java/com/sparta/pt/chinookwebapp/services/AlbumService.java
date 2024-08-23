package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.converters.AlbumDTOConverter;
import com.sparta.pt.chinookwebapp.dtos.AlbumDTO;
import com.sparta.pt.chinookwebapp.models.Album;
import com.sparta.pt.chinookwebapp.repositories.AlbumRepository;
import com.sparta.pt.chinookwebapp.utils.IdManagementUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final AlbumDTOConverter albumDtoConverter;
    private final IdManagementUtils idManagementUtils;

    @Autowired
    public AlbumService(AlbumRepository albumRepository, AlbumDTOConverter albumDtoConverter, IdManagementUtils idManagementUtils) {
        this.albumRepository = albumRepository;
        this.albumDtoConverter = albumDtoConverter;
        this.idManagementUtils = idManagementUtils;
    }

    public Page<AlbumDTO> getAllAlbums(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return albumRepository.findAll(pageable).map(albumDtoConverter::convertToDTO);
    }

    public Optional<AlbumDTO> getAlbumById(Integer id) {
        return albumRepository.findById(id).map(albumDtoConverter::convertToDTO);
    }

    public Optional<AlbumDTO> getAlbumByTitle(String title) {
        return albumRepository.findByTitle(title).map(albumDtoConverter::convertToDTO);
    }

    public AlbumDTO createAlbum(AlbumDTO albumDTO) {
        Album album = albumDtoConverter.convertToEntity(albumDTO);

        List<Album> allAlbums = albumRepository.findAll();
        int newId = idManagementUtils.generateId(allAlbums, Album::getId);
        album.setId(newId);

        Album savedAlbum = albumRepository.save(album);
        return albumDtoConverter.convertToDTO(savedAlbum);
    }

    public Optional<AlbumDTO> upsertAlbum(Integer id, AlbumDTO albumDTO) {
        Album album = albumRepository.findById(id).orElse(new Album());
        album.setId(id);
        album.setTitle(albumDTO.getTitle());

        albumDtoConverter.setArtistByName(album, albumDTO.getArtistName());

        Album savedAlbum = albumRepository.save(album);
        return Optional.of(albumDtoConverter.convertToDTO(savedAlbum));
    }

    public Optional<AlbumDTO> patchAlbum(Integer id, AlbumDTO albumDTO) {
        return albumRepository.findById(id).map(existingAlbum -> {
            if (albumDTO.getTitle() != null) {
                existingAlbum.setTitle(albumDTO.getTitle());
            }
            if (albumDTO.getArtistName() != null) {
                albumDtoConverter.setArtistByName(existingAlbum, albumDTO.getArtistName());
            }
            Album savedAlbum = albumRepository.save(existingAlbum);
            return albumDtoConverter.convertToDTO(savedAlbum);
        });
    }

    public boolean deleteAlbum(Integer id) {
        if (albumRepository.existsById(id)) {
            albumRepository.deleteById(id);
            return true;
        }
        return false;
    }
}