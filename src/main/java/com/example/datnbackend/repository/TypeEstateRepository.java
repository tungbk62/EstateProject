package com.example.datnbackend.repository;

import com.example.datnbackend.entity.TypeEstateEntity;
import com.example.datnbackend.entity.TypeReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TypeEstateRepository extends JpaRepository<TypeEstateEntity, Long> {
    @Query(value = "SELECT t.* FROM type_estate t " +
            "WHERE t.id IN ?1", nativeQuery = true)
    List<TypeEstateEntity> findAllByIdIn(List<Long> ids);

    @Query(value = "DELETE FROM type_estate t " +
            "WHERE t.id IN ?1", nativeQuery = true)
    @Modifying
    void deleteByIdIn(List<Long> ids);
}
