package com.example.datnbackend.repository;

import com.example.datnbackend.entity.TypeReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TypeReportRepository extends JpaRepository<TypeReportEntity, Long> {
    @Query(value = "SELECT t.* FROM type_report t " +
            "WHERE t.id IN ?1", nativeQuery = true)
    List<TypeReportEntity> findAllByIdIn(List<Long> ids);

    @Query(value = "DELETE FROM type_report t " +
            "WHERE t.id IN ?1", nativeQuery = true)
    @Modifying
    void deleteByIdIn(List<Long> ids);

}
