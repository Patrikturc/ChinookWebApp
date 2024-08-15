package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.dtos.MediatypeDTO;
import com.sparta.pt.chinookwebapp.models.Mediatype;
import com.sparta.pt.chinookwebapp.repositories.MediatypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MediatypeService {

    private final MediatypeRepository mediatypeRepository;

    @Autowired
    public MediatypeService(MediatypeRepository mediatypeRepository) {
        this.mediatypeRepository = mediatypeRepository;
    }

    public List<MediatypeDTO> getAllMediatypes() {
        return mediatypeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<MediatypeDTO> getMediatypeById(Integer id) {
        return mediatypeRepository.findById(id)
                .map(this::convertToDTO);
    }

    public MediatypeDTO createMediatype(MediatypeDTO mediatypeDTO) {
        Mediatype mediatype = new Mediatype();
        mediatype.setName(mediatypeDTO.getName());

        List<Mediatype> allMediatypes = mediatypeRepository.findAll();
        int maxId = allMediatypes.stream()
                .max(Comparator.comparingInt(Mediatype::getId))
                .map(Mediatype::getId)
                .orElse(0);

        mediatype.setId(maxId + 1);
        Mediatype savedMediatype = mediatypeRepository.save(mediatype);
        return convertToDTO(savedMediatype);
    }

    public MediatypeDTO upsertMediatype(Integer id, MediatypeDTO mediatypeDTO) {
        Mediatype mediatype = mediatypeRepository.findById(id)
                .orElse(new Mediatype());
        mediatype.setId(id);
        mediatype.setName(mediatypeDTO.getName());
        Mediatype savedMediatype = mediatypeRepository.save(mediatype);
        return convertToDTO(savedMediatype);
    }

    public Optional<MediatypeDTO> patchMediatype(Integer id, MediatypeDTO mediatypeDTO) {
        return mediatypeRepository.findById(id)
                .map(existingMediatype -> {
                    if (mediatypeDTO.getName() != null) {
                        existingMediatype.setName(mediatypeDTO.getName());
                    }
                    Mediatype savedMediatype = mediatypeRepository.save(existingMediatype);
                    return convertToDTO(savedMediatype);
                });
    }

    public boolean deleteMediatype(Integer id) {
        if (mediatypeRepository.existsById(id)) {
            mediatypeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private MediatypeDTO convertToDTO(Mediatype mediatype) {
        return new MediatypeDTO(
                mediatype.getId(),
                mediatype.getName()
        );
    }
}