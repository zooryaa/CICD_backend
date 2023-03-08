package com.example.demo.core.generic;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface AbstractQueryService<T extends AbstractEntity> {

    List<T> findAll();

    List<T> findAll(Pageable pageable);

    T findById(UUID id);

    boolean existsById(UUID id);

}
