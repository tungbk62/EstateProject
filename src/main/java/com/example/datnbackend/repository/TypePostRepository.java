package com.example.datnbackend.repository;

import com.example.datnbackend.entity.TypePostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypePostRepository extends JpaRepository<TypePostEntity, Long> {
    TypePostEntity findOneById(Long id);
}
