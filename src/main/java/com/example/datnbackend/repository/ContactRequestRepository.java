package com.example.datnbackend.repository;

import com.example.datnbackend.entity.ContactRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRequestRepository extends JpaRepository<ContactRequestEntity, Long> {
}
