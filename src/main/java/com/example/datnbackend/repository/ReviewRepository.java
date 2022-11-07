package com.example.datnbackend.repository;

import com.example.datnbackend.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    @Query(value = "SELECT * FROM review r WHERE " +
            "r.user_business_id = ?1 " +
            "AND r.deleted = 0", nativeQuery = true)
    List<ReviewEntity> findAllByUserBusinessIdAndDeletedFalse(Long id);
}
