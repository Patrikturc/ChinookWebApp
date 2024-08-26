package com.sparta.pt.chinookwebapp.utils;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Collection;
import java.util.List;

public record CustomPagedResponse<T>(com.sparta.pt.chinookwebapp.utils.CustomPagedResponse.PageMetadata page,
                                     Collection<EntityModel<T>> content, List<Link> links) {

    public record PageMetadata(int size, long totalElements, int totalPages, int number) {
    }
}