package com.sparta.pt.chinookwebapp.utils;

import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class PaginationUtils<T> {

    private final PagedResourcesAssembler<T> pagedResourcesAssembler;

    public PaginationUtils(PagedResourcesAssembler<T> pagedResourcesAssembler) {
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    public ResponseEntity<CustomPagedResponse<T>> createPagedResponse(Page<T> page) {
        PagedModel<EntityModel<T>> pagedModel = pagedResourcesAssembler.toModel(page, EntityModel::of);

        CustomPagedResponse.PageMetadata pageMetadata = new CustomPagedResponse.PageMetadata(
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber()
        );

        CustomPagedResponse<T> customResponse = new CustomPagedResponse<>(
                pageMetadata,
                pagedModel.getContent(),
                pagedModel.getLinks().toList()
        );

        return ResponseEntity.ok(customResponse);
    }
}