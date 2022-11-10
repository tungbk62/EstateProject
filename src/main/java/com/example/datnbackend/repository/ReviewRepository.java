package com.example.datnbackend.repository;

import com.example.datnbackend.entity.ReviewEntity;
import org.springframework.data.domain.Pageable;
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
    @Query(value = "SELECT * FROM review r WHERE " +
            "r.user_business_id = ?1 " +
            "AND r.deleted = 0 " +
            "ORDER BY " +
            "CASE WHEN ?2 IS NULL THEN NULL END, " +
            "CASE WHEN ?2 = 'DATEDESC' THEN r.created_date END DESC", nativeQuery = true)
    List<ReviewEntity> findAllByUserBusinessIdAndDeletedFalseWithPaging(Long id, String order, Pageable pageable);

    ReviewEntity findOneByIdAndDeletedFalse(Long id);
    @Query(value = "SELECT * FROM review r " +
            "WHERE r.deleted = 0 " +
            "AND r.id IN (?1)", nativeQuery = true)
    List<ReviewEntity> findAllByIdInAndDeletedFalse(List<Long> ids);
}
