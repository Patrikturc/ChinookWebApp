package com.sparta.pt.chinookwebapp.converters;

public abstract class BaseDTOConverter<E, D> implements DTOConverter<E, D> {

    @Override
    public D convertToDTO(E entity) {
        throw new UnsupportedOperationException("Conversion to DTO not implemented");
    }

    @Override
    public E convertToEntity(D dto) {
        throw new UnsupportedOperationException("Conversion to Entity not implemented");
    }
}