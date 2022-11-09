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
            "CASE WHEN :order IS 'DATEDESC' THEN cr.created_date END DESC")
    List<ContactRequestEntity> findAllByUserBusinessIdAndFilter(@Param("id") Long id,
                                                                @Param("order") String order,
                                                                @Param("viewed") Integer viewed,
                                                                @Param("handled") Integer handled, Pageable pageable);
    ContactRequestEntity findOneByIdWithDeletedFalse(Long id);
}
