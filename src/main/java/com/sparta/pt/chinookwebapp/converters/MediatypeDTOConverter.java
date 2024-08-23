package com.sparta.pt.chinookwebapp.converters;

import com.sparta.pt.chinookwebapp.dtos.MediatypeDTO;
import com.sparta.pt.chinookwebapp.models.Mediatype;
import org.springframework.stereotype.Component;

@Component
public class MediatypeDTOConverter extends BaseDTOConverter<Mediatype, MediatypeDTO> {

    @Override
    public MediatypeDTO convertToDTO(Mediatype mediaType) {
        return new MediatypeDTO(mediaType.getId(), mediaType.getName());
    }

    @Override
    public Mediatype convertToEntity(MediatypeDTO mediaTypeDTO) {
        Mediatype mediaType = new Mediatype();
        mediaType.setId(mediaTypeDTO.getId());
        mediaType.setName(mediaTypeDTO.getName());
        return mediaType;
    }
}
