package com.example.datnbackend.repository;

import com.example.datnbackend.entity.PostReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostReportRepository extends JpaRepository<PostReportEntity, Long> {
}
