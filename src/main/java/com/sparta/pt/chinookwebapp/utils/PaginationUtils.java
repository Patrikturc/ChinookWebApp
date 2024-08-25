package com.sparta.pt.chinookwebapp.utils;

import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PaginationUtils<T> {

    private final PagedResourcesAssembler<T> pagedResourcesAssembler;

    public PaginationUtils(PagedResourcesAssembler<T> pagedResourcesAssembler) {
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    public ResponseEntity<PagedModel<EntityModel<T>>> createPagedResponse(Page<T> page, Class<?> controllerClass, Function<T, Object> idExtractor) {
        PagedModel<EntityModel<T>> pagedModel = pagedResourcesAssembler.toModel(page,
                entity -> {
                    Object id = idExtractor.apply(entity);
                    return EntityModel.of(entity);
                });

        return ResponseEntity.ok(pagedModel);
    }
}