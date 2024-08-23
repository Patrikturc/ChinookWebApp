package com.sparta.pt.chinookwebapp.converters;

public interface DtoConverter<E, D> {
    D convertToDTO(E entity);
    E convertToEntity(D dto);
}