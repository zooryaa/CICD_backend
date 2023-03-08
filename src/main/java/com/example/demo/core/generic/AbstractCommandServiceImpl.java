package com.example.demo.core.generic;

import java.util.NoSuchElementException;
import java.util.UUID;

public class AbstractCommandServiceImpl<T extends AbstractEntity> implements AbstractCommandService<T> {
    protected final AbstractRepository<T> repository;


    protected AbstractCommandServiceImpl(AbstractRepository<T> repository) {
        this.repository = repository;
    }

    @Override
    public T save(T entity) {
        return repository.save(entity);
    }

    @Override
    public void deleteById(UUID id) throws NoSuchElementException {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        } else {
            throw new NoSuchElementException(String.format("Entity with ID '%s' could not be found", id));
        }
    }

    @Override
    public T updateById(UUID id, T entity) throws NoSuchElementException {
        if (repository.existsById(id)) {
            entity.setId(id);
            return repository.save(entity);
        } else {
            throw new NoSuchElementException(String.format("Entity with ID '%s' could not be found", id));
        }
    }

}
