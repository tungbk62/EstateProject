package com.example.datnbackend.repository;

import com.example.datnbackend.entity.TypeNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TypeNotificationRepository extends JpaRepository<TypeNotificationEntity, Long> {
    TypeNotificationEntity findOneById(Long id);
}
