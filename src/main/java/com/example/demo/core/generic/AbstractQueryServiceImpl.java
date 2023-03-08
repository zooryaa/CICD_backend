package com.example.demo.core.generic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public abstract class AbstractQueryServiceImpl<T extends AbstractEntity> implements AbstractQueryService<T> {

    protected final AbstractRepository<T> repository;

    protected AbstractQueryServiceImpl(AbstractRepository<T> repository) {
        this.repository = repository;
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public List<T> findAll(Pageable pageable) {
        Page<T> pagedResult = repository.findAll(pageable);
        return pagedResult.hasContent() ? pagedResult.getContent() : new ArrayList<>();
    }

    @Override
    public T findById(UUID id) {
        return repository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public boolean existsById(UUID id) {
        return repository.existsById(id);
    }

}
