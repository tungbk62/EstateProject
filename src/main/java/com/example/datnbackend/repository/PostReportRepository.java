package com.example.datnbackend.repository;

import com.example.datnbackend.entity.PostReportEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostReportRepository extends JpaRepository<PostReportEntity, Long> {
    @Query(value = "SELECT pr.* FROM post_report pr " +
            "JOIN post p ON pr.post_id = p.id " +
            "WHERE pr.deleted = 0 " +
            "AND ((:postId IS NULL) OR (pr.post_id = :postId)) " +
            "AND ((:typeId IS NULL) OR (pr.type_report_id = :typeId)) " +
            "AND ((:userId IS NULL) OR (p.created_by = :userId)) " +
            "AND ((:viewed IS NULL) OR (pr.viewed = :viewed)) " +
            "AND ((:handled IS NULL) OR (pr.handled = :handled)) " +
            "ORDER BY " +
            "CASE WHEN :order IS NULL THEN NULL END, " +
            "CASE WHEN :order = 'DATEDESC' THEN pr.created_date END DESC", nativeQuery = true)
    List<PostReportEntity> findAllWithFilter(@Param("order") String order, @Param("postId") Long postId, @Param("typeId") Long typeId,
                                             @Param("userId") Long userId, @Param("viewed") Integer viewed, @Param("handled") Integer handled,
                                             Pageable pageable);

    PostReportEntity findOneByIdAndDeletedFalse(Long id);
    @Query(value = "SELECT pr.* FROM post_report pr " +
            "WHERE pr.deleted = 0 AND pr.id IN (?1)")
    List<PostReportEntity> findAllByIdInAndDeletedFalse(List<Long> ids);
}
