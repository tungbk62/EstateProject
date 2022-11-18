package com.example.datnbackend.repository;

import com.example.datnbackend.entity.NotificationEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    NotificationEntity findOneByIdAndDeletedFalse(Long id);

    @Query(value = "SELECT n.* FROM notification n " +
            "WHERE n.id = ?1 " +
            "AND n.user_id = ?2 " +
            "AND n.deleted = 0", nativeQuery = true)
    NotificationEntity findOneByIdAndUserIdAndDeletedFalse(Long id, Long userId);

    @Query(value = "SELECT n.* FROM notification n " +
            "WHERE n.deleted = 0 " +
            "AND ((?1 IS NULL) OR (n.user_id = ?1)) " +
            "AND ((?2 IS NULL) OR (n.created_by = ?2)) " +
            "AND ((?3 IS NULL) OR (n.type_id = ?3))", nativeQuery = true)
    List<NotificationEntity> findAllByCreatedByAndDeletedFalse(Long userId, Long createdBy, Long typeId, Pageable pageable);

    @Query(value = "SELECT n.* FROM notification n " +
            "WHERE n.deleted = 0 " +
            "AND n.user_id = ?1 " +
            "AND ((?2 IS NULL) OR (n.type_id = ?2)) " +
            "AND ((?3 IS NULL) OR (n.viewed = ?3))", nativeQuery = true)
    List<NotificationEntity> findAllByUserAndDeletedFalse(Long userId, Long typeId, Integer viewed, Pageable pageable);

    @Query(value = "SELECT n.* FROM notification n " +
            "WHERE n.deleted = 0 " +
            "AND n.created_by = ?2 " +
            "AND n.id IN (?1)", nativeQuery = true)
    List<NotificationEntity> findAllByIdInAndCreatedByAndDeletedFalse(List<Long> ids, Long createdBy);
}
