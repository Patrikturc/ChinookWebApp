package com.sparta.pt.chinookwebapp.services;

import com.sparta.pt.chinookwebapp.utils.HateoasUtils;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilderFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class BaseService<T, D, R extends JpaRepository<T, Integer>> {

    protected final R repository;
    protected final HateoasUtils<D> hateoasUtils;
    protected final WebMvcLinkBuilderFactory linkBuilderFactory;

    public BaseService(R repository, HateoasUtils<D> hateoasUtils, WebMvcLinkBuilderFactory linkBuilderFactory) {
        this.repository = repository;
        this.hateoasUtils = hateoasUtils;
        this.linkBuilderFactory = linkBuilderFactory;
    }

    public ResponseEntity<PagedModel<EntityModel<D>>> getAll(Pageable pageable, Function<T, D> toDto, Class<?> controllerClass, Function<D, Object> idExtractor, BiFunction<D, WebMvcLinkBuilder, WebMvcLinkBuilder> customLinkBuilder) {
        Page<T> entities = repository.findAll(pageable);
        Page<D> dtoPage = entities.map(toDto);
        return hateoasUtils.createPagedResponseWithCustomLinks(
                dtoPage,
                controllerClass,
                idExtractor,
                Optional.of(customLinkBuilder),
                "custom"
        );
    }

    public ResponseEntity<EntityModel<D>> getById(Integer id, Function<T, D> toDto, Class<?> controllerClass, Function<D, Object> idExtractor, BiFunction<D, WebMvcLinkBuilder, WebMvcLinkBuilder> customLinkBuilder) {
        return repository.findById(id)
                .map(entity -> {
                    D dto = toDto.apply(entity);
                    return hateoasUtils.createEntityResponse(
                            dto,
                            controllerClass,
                            idExtractor,
                            customLinkBuilder,
                            "custom"
                    );
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<EntityModel<D>> create(T entity, Function<T, D> toDto, Class<?> controllerClass, Function<D, Object> idExtractor, BiFunction<D, WebMvcLinkBuilder, WebMvcLinkBuilder> customLinkBuilder) {
        setNextId(entity);
        T savedEntity = repository.save(entity);
        D dto = toDto.apply(savedEntity);
        return getById((Integer) idExtractor.apply(dto), toDto, controllerClass, idExtractor, customLinkBuilder);
    }

    public ResponseEntity<EntityModel<D>> update(Integer id, T entityDetails, Function<T, D> toDto, Class<?> controllerClass, Function<D, Object> idExtractor, BiFunction<D, WebMvcLinkBuilder, WebMvcLinkBuilder> customLinkBuilder) {
        Optional<T> updatedEntity = repository.findById(id)
                .map(existingEntity -> {
                    updateEntity(existingEntity, entityDetails);
                    return repository.save(existingEntity);
                });

        return updatedEntity.map(entity -> getById(id, toDto, controllerClass, idExtractor, customLinkBuilder))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<Void> delete(Integer id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    protected abstract void updateEntity(T existingEntity, T entityDetails);

    private void setNextId(T entity) {
        List<T> allEntities = repository.findAll();
        int maxId = allEntities.stream()
                .max(Comparator.comparingInt(e -> (Integer) getId(e)))
                .map(e -> (Integer) getId(e))
                .orElse(0);
        setId(entity, maxId + 1);
    }

    protected Object getId(T entity) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            return idField.get(entity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to get ID field", e);
        }
    }

    protected void setId(T entity, Integer id) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set ID field", e);
        }
    }
}