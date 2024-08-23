package com.sparta.pt.chinookwebapp.converters;

public interface DTOConverter<E, D> {
    D convertToDTO(E entity);
    E convertToEntity(D dto);
}