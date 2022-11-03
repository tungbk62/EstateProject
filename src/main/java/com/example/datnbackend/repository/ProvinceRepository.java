package com.example.datnbackend.repository;

import com.example.datnbackend.entity.ProvinceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvinceRepository extends JpaRepository<ProvinceEntity, Long> {
    ProvinceEntity findOneById(Long id);
}
