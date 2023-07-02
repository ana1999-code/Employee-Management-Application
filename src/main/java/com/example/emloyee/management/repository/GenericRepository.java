package com.example.emloyee.management.repository;

import java.util.List;
import java.util.Optional;

public interface GenericRepository<T, ID> {

    List<T> findAll();

    Optional<T> findById(ID id);

    void save(T object);

    void deleteById(ID id);

    void update(T object, ID id);

    void deleteAll();
}
