package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.converters.MediatypeDtoConverter;
import com.sparta.pt.chinookwebapp.dtos.MediatypeDTO;
import com.sparta.pt.chinookwebapp.models.Mediatype;
import com.sparta.pt.chinookwebapp.repositories.MediatypeRepository;
import com.sparta.pt.chinookwebapp.utils.IdManagementUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MediatypeService {

    private final MediatypeRepository mediatypeRepository;
    private final MediatypeDtoConverter mediatypeDtoConverter;
    private final IdManagementUtils idManagementUtils;

    @Autowired
    public MediatypeService(MediatypeRepository mediatypeRepository, MediatypeDtoConverter mediatypeDtoConverter, IdManagementUtils idManagementUtils) {
        this.mediatypeRepository = mediatypeRepository;
        this.mediatypeDtoConverter = mediatypeDtoConverter;
        this.idManagementUtils = idManagementUtils;
    }

    public List<MediatypeDTO> getAllMediatypes() {
        return mediatypeRepository.findAll().stream()
                .map(mediatypeDtoConverter::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<MediatypeDTO> getMediatypeById(Integer id) {
        return mediatypeRepository.findById(id)
                .map(mediatypeDtoConverter::convertToDTO);
    }

    public Optional<MediatypeDTO> getMediatypeByName(String name) {
        return mediatypeRepository.findByName(name)
                .map(mediatypeDtoConverter::convertToDTO);
    }

    public MediatypeDTO createMediatype(MediatypeDTO mediatypeDTO) {
        Mediatype mediatype = new Mediatype();
        mediatype.setName(mediatypeDTO.getName());

        List<Mediatype> allMediatypes = mediatypeRepository.findAll();
        int newId = idManagementUtils.generateId(allMediatypes, Mediatype::getId);
        mediatype.setId(newId);

        Mediatype savedMediatype = mediatypeRepository.save(mediatype);
        return mediatypeDtoConverter.convertToDTO(savedMediatype);
    }

    public MediatypeDTO upsertMediatype(Integer id, MediatypeDTO mediatypeDTO) {
        Mediatype mediatype = mediatypeRepository.findById(id)
                .orElse(new Mediatype());
        mediatype.setId(id);
        mediatype.setName(mediatypeDTO.getName());
        Mediatype savedMediatype = mediatypeRepository.save(mediatype);
        return mediatypeDtoConverter.convertToDTO(savedMediatype);
    }

    public Optional<MediatypeDTO> patchMediatype(Integer id, MediatypeDTO mediatypeDTO) {
        return mediatypeRepository.findById(id)
                .map(existingMediatype -> {
                    if (mediatypeDTO.getName() != null) {
                        existingMediatype.setName(mediatypeDTO.getName());
                    }
                    Mediatype savedMediatype = mediatypeRepository.save(existingMediatype);
                    return mediatypeDtoConverter.convertToDTO(savedMediatype);
                });
    }

    public boolean deleteMediatype(Integer id) {
        if (mediatypeRepository.existsById(id)) {
            mediatypeRepository.deleteById(id);
            return true;
        }
        return false;
    }
}