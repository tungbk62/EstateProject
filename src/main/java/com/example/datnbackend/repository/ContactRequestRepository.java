package com.example.datnbackend.repository;

import com.example.datnbackend.entity.ContactRequestEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRequestRepository extends JpaRepository<ContactRequestEntity, Long> {
    @Query(value = "SELECT cr.* FROM contact_request cr " +
            "WHERE cr.user_business_id = :id AND cr.deleted = 0 " +
            "AND ((:viewed IS NULL) OR (cr.viewed = :viewed)) " +
            "AND ((:handled IS NULL) OR (cr.handled = :handled)) " +
            "ORDER BY " +
            "CASE WHEN :order IS NULL THEN NULL END, " +
            "CASE WHEN :order = 'DATEDESC' THEN cr.created_date END DESC", nativeQuery = true)
    List<ContactRequestEntity> findAllByUserBusinessIdAndFilter(@Param("id") Long id,
                                                                @Param("order") String order,
                                                                @Param("viewed") Integer viewed,
                                                                @Param("handled") Integer handled, Pageable pageable);
    ContactRequestEntity findOneByIdAndDeletedFalse(Long id);
    @Query(value = "SELECT cr.* FROM contact_request cr " +
            "WHERE cr.user_business_id = ?2 AND cr.deleted = 0 " +
            "AND cr.id IN (?1)", nativeQuery = true)
    List<ContactRequestEntity> findAllByIdInAndUserBusinessIdAndDeletedFalse(List<Long> ids, Long userId);
}
