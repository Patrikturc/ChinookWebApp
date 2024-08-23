package com.sparta.pt.chinookwebapp.utils;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class IdManagementUtils {

    public synchronized <T> int generateId(List<T> entities, IdExtractor<T> extractor) {
        return entities.stream()
                .map(extractor::getId)
                .max(Integer::compareTo)
                .map(id -> id + 1)
                .orElse(1);
    }

    @FunctionalInterface
    public interface IdExtractor<T> {
        int getId(T entity);
    }
}