package com.example.datnbackend.repository;

import com.example.datnbackend.entity.PostEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    PostEntity findOneByIdAndDeletedFalseAndHideFalseAndLockedFalse(Long id);
    PostEntity findOneByIdAndDeletedFalseAndLockedFalse(Long id);
    PostEntity findOneByIdAndDeletedFalse(Long id);

    @Query(value = "SELECT p.* FROM post p " +
            "JOIN wards w ON p.wards_id = w.id " +
            "JOIN district d ON w.district_id = d.id " +
            "JOIN province pr ON d.province_id = pr.id " +
            "WHERE ((:province IS NULL) OR (pr.id = :province)) " +
            "AND ((:district IS NULL) OR (d.id = :district)) " +
            "AND ((:wards IS NULL) OR (w.id = :wards)) " +
            "AND ((:address IS NULL) OR ( UPPER(p.address_detail) LIKE UPPER(CONCAT('%', :address ,'%')))) " +
            "AND ((:type IS NULL) OR (p.type_estate_id IN (:type))) " +
            "AND ((:room IS NULL) OR (p.room = :room)) " +
            "AND ((:pricemin IS NULL) OR (:pricemax IS NULL) OR ((p.price_month >= :pricemin) AND (p.price_month <= :pricemax))) " +
            "AND p.deleted = 0 AND p.locked = 0 AND p.hide = 0 " +
            "ORDER BY p.created_date DESC",
    nativeQuery = true)
    List<PostEntity> findAllWithFilterWithDeletedFalseAndHideFalseAndLockedFalse(@Param("province") Long province, @Param("district") Long district,
                                                                                 @Param("wards") Long wards, @Param("address") String address,
                                                                                 @Param("type") List<Long> type, @Param("room") Integer room,
                                                                                 @Param("pricemin") Double pricemin,
                                                                                 @Param("pricemax") Double pricemax, Pageable pageable);
}
