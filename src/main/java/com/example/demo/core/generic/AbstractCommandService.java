package com.example.demo.core.generic;

import java.util.NoSuchElementException;
import java.util.UUID;

public interface AbstractCommandService<T extends AbstractEntity> {

    T save(T entity);

    void deleteById(UUID id) throws NoSuchElementException;

    T updateById(UUID id, T entity) throws NoSuchElementException;

}