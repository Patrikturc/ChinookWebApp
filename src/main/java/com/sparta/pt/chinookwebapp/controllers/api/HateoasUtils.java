package com.sparta.pt.chinookwebapp.controllers.api;

import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilderFactory;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@Component
public class HateoasUtils<T> {

    private final PagedResourcesAssembler<T> pagedResourcesAssembler;
    private final WebMvcLinkBuilderFactory linkBuilderFactory;

    public HateoasUtils(PagedResourcesAssembler<T> pagedResourcesAssembler,
                        WebMvcLinkBuilderFactory linkBuilderFactory) {
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.linkBuilderFactory = linkBuilderFactory;
    }

    public ResponseEntity<PagedModel<EntityModel<T>>> createPagedResponse(Page<T> page, Class<?> controllerClass, Function<T, Object> idExtractor) {
        PagedModel<EntityModel<T>> pagedModel = pagedResourcesAssembler.toModel(page,
                entity -> {
                    Object id = idExtractor.apply(entity);
                    WebMvcLinkBuilder linkBuilder = linkBuilderFactory.linkTo(controllerClass).slash(id);
                    return EntityModel.of(entity, linkBuilder.withSelfRel());
                });

        return ResponseEntity.ok(pagedModel);
    }

    public ResponseEntity<PagedModel<EntityModel<T>>> createPagedResponseWithCustomLinks(Page<T> page, Class<?> controllerClass, Function<T, Object> idExtractor, BiFunction<T, WebMvcLinkBuilder, WebMvcLinkBuilder> linkBuilderFunction) {
        PagedModel<EntityModel<T>> pagedModel = pagedResourcesAssembler.toModel(page,
                entity -> {
                    Object id = idExtractor.apply(entity);
                    WebMvcLinkBuilder selfLinkBuilder = linkBuilderFactory.linkTo(controllerClass).slash(id);
                    WebMvcLinkBuilder customLinkBuilder = linkBuilderFunction.apply(entity, selfLinkBuilder);
                    return EntityModel.of(entity, selfLinkBuilder.withSelfRel(), customLinkBuilder.withRel("custom-rel"));
                });

        return ResponseEntity.ok(pagedModel);
    }

    public ResponseEntity<EntityModel<T>> createEntityResponse(T entity, Class<?> controllerClass, Function<T, Object> idExtractor, BiFunction<T, WebMvcLinkBuilder, WebMvcLinkBuilder> linkBuilderFunction) {
        Object id = idExtractor.apply(entity);
        WebMvcLinkBuilder selfLinkBuilder = linkBuilderFactory.linkTo(controllerClass).slash(id);
        WebMvcLinkBuilder customLinkBuilder = linkBuilderFunction.apply(entity, selfLinkBuilder);
        EntityModel<T> entityModel = EntityModel.of(entity, selfLinkBuilder.withSelfRel(), customLinkBuilder.withRel("custom-rel"));
        return ResponseEntity.ok(entityModel);
    }

}