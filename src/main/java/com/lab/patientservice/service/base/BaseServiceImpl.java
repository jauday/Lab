package com.lab.patientservice.service.base;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.lab.patientservice.model.BaseEntity;

import jakarta.persistence.EntityNotFoundException;

public abstract class BaseServiceImpl<T extends BaseEntity, ID> implements BaseService<T, ID> {

    protected final JpaRepository<T, ID> repository;

    protected BaseServiceImpl(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    @Override
    public List<T> getAll() {
        return repository.findAll().stream()
                .filter(BaseEntity::getActive)
                .toList();
    }

    @Override
    public Optional<T> getById(ID id) {
        return repository.findById(id).filter(BaseEntity::getActive);
    }

    @Override
    public T save(T entity) {
        return repository.save(entity);
    }

    @Override
    public void deleteById(ID id) {
        T entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found"));
        entity.setActive(false);
        repository.save(entity);
    }

    @Override
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

    public Optional<Object> findByCodeAndActiveTrue(String code) {
        return null;
    }
}

