package com.example.datnbackend.repository;

import com.example.datnbackend.entity.WardsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WardsRepository extends JpaRepository<WardsEntity, Long> {
    WardsEntity findOneById(Long id);
}
