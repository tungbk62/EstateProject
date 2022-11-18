package com.example.datnbackend.repository;

import com.example.datnbackend.entity.PostEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
            "JOIN type_post tp ON p.type_post_id = tp.id " +
            "WHERE ((:province IS NULL) OR (pr.id = :province)) " +
            "AND ((:district IS NULL) OR (d.id = :district)) " +
            "AND ((:wards IS NULL) OR (w.id = :wards)) " +
            "AND ((COALESCE(:type) IS NULL) OR (p.type_estate_id IN (:type))) " +
            "AND ((:room IS NULL) OR (p.room = :room)) " +
            "AND ((:pricemin IS NULL) OR (:pricemax IS NULL) OR ((p.price_month >= :pricemin) AND (p.price_month <= :pricemax))) " +
            "AND ((:areamin IS NULL) OR (:areamax IS NULL) OR ((p.area >= :areamin) AND (p.area <= :areamax))) " +
            "AND p.deleted = 0 AND p.locked = 0 AND p.hide = 0 " +
            "ORDER BY " +
            "CASE WHEN tp.id = 1 THEN 1 " +
            "WHEN tp.id = 2 THEN 2 " +
            "WHEN tp.id = 3 THEN 3 " +
            "ELSE 4 END ASC, " +
            "CASE WHEN :order IS NULL THEN NULL END, " +
            "CASE WHEN :order = 'DATEASC' THEN p.created_date END ASC, " +
            "CASE WHEN :order = 'DATEDESC' THEN p.created_date END DESC, " +
            "CASE WHEN :order = 'PRICEASC' THEN p.price_month END ASC, " +
            "CASE WHEN :order = 'PRICEDESC' THEN p.price_month END DESC, " +
            "CASE WHEN :order = 'AREAASC' THEN p.area END ASC, " +
            "CASE WHEN :order = 'AREADESC' THEN p.area END DESC"
    ,nativeQuery = true)
    List<PostEntity> findAllWithFilterWithDeletedFalseAndHideFalseAndLockedFalse(@Param("order") String order, @Param("province") Long province,
                                                                                 @Param("district") Long district,
                                                                                 @Param("wards") Long wards,
                                                                                 @Param("type") List<Long> type, @Param("room") Integer room,
                                                                                 @Param("pricemin") Double pricemin,
                                                                                 @Param("pricemax") Double pricemax,
                                                                                 @Param("areamin") Double areamin,
                                                                                 @Param("areamax") Double areamax, Pageable pageable);

    @Query(value = "SELECT p.* FROM post p " +
            "JOIN wards w ON p.wards_id = w.id " +
            "JOIN district d ON w.district_id = d.id " +
            "JOIN province pr ON d.province_id = pr.id " +
            "JOIN type_estate te ON p.type_estate_id = te.id " +
            "JOIN type_post tp ON p.type_post_id = tp.id " +
            "WHERE ((:search IS NULL) OR (" +
            "(UPPER(w.name) LIKE CONCAT('%', :search, '%')) " +
            "OR (UPPER(d.name) LIKE CONCAT('%', :search, '%')) " +
            "OR (UPPER(pr.name) LIKE CONCAT('%', :search, '%')) " +
            "OR (UPPER(p.address_detail) LIKE CONCAT('%', :search, '%')) " +
            "OR (UPPER(p.title) LIKE CONCAT('%', :search, '%')) " +
            "OR (UPPER(te.name) LIKE CONCAT('%', :search, '%')))) " +
            "AND p.deleted = 0 AND p.locked = 0 AND p.hide = 0 " +
            "ORDER BY " +
            "CASE WHEN tp.id = 1 THEN 1 " +
            "WHEN tp.id = 2 THEN 2 " +
            "WHEN tp.id = 3 THEN 3 " +
            "ELSE 4 END ASC, " +
            "CASE WHEN :order IS NULL THEN NULL END, " +
            "CASE WHEN :order = 'DATEASC' THEN p.created_date END ASC, " +
            "CASE WHEN :order = 'DATEDESC' THEN p.created_date END DESC, " +
            "CASE WHEN :order = 'PRICEASC' THEN p.price_month END ASC, " +
            "CASE WHEN :order = 'PRICEDESC' THEN p.price_month END DESC, " +
            "CASE WHEN :order = 'AREAASC' THEN p.area END ASC, " +
            "CASE WHEN :order = 'AREADESC' THEN p.area END DESC"
            ,nativeQuery = true)
    List<PostEntity> findAllWithSearchWithDeletedFalseAndHideFalseAndLockedFalse(@Param("order") String order,
                                                                                 @Param("search") String search,
                                                                                 Pageable pageable);

    @Query(value = "SELECT p.* FROM post p " +
            "WHERE p.deleted = 0 " +
            "AND p.created_by = ?1 " +
            "AND ((?2 IS NULL) OR (p.type_post_id = ?2))", nativeQuery = true)
    List<PostEntity> findAllByCreatedByIdAndDeletedFalse(Long id, Long typePostId,  Pageable pageable);

    @Query(value = "SELECT p.* FROM post p " +
            "WHERE p.deleted = 0 " +
            "AND ((?1 IS NULL) OR (p.created_by = ?1)) " +
            "AND ((?2 IS NULL) OR (p.type_post_id = ?2))", nativeQuery = true)
    List<PostEntity> findAllByDeletedFalse(Long userId, Long typePostId, Pageable pageable);

    @Query(value = "SELECT p.* FROM post p " +
            "JOIN post_save ps ON p.id = ps.post_id " +
            "WHERE ps.user_id = ?1 AND p.deleted = 0 AND p.hide = 0 AND p.locked = 0", nativeQuery = true)
    List<PostEntity> findAllByUserIdWithDeletedFalseAndHideFalseAndLockedFalse(Long id, Pageable pageable);

    @Query(value = "SELECT p.* FROM post p " +
            "JOIN post_save ps ON p.id = ps.post_id " +
            "WHERE ps.user_id = ?1 AND p.id IN (?2)", nativeQuery = true)
    List<PostEntity> findAllByUserIdNoPaging(Long id, List<Long> ids);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM post_save ps WHERE ps.user_id = ?1 AND ps.post_id IN (?2)", nativeQuery = true)
    void deleteByUserIdAndPostIdIn(Long userId, List<Long> ids);
}
