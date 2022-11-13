package com.example.datnbackend.repository;

import com.example.datnbackend.entity.PostImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostImageRepository extends JpaRepository<PostImageEntity, Long> {
    @Query(value = "SELECT pi FROM PostImageEntity pi " +
            "WHERE pi.post.id = ?1 " +
            "AND pi.deleted = FALSE ")
    List<PostImageEntity> findAllByPostIdAndDeletedFalse(Long postId);
    @Query(value = "SELECT pi FROM PostImageEntity pi " +
            "WHERE pi.post.id = ?1 " +
            "AND pi.deleted = FALSE " +
            "AND pi.mainImage = TRUE")
    PostImageEntity findOneByPostIdAndDeletedFalseAndMainImageTrue(Long postId);

    PostImageEntity findOneByIdAndDeletedFalse(Long id);
    @Query(value = "SELECT pi.* FROM post_image pi " +
            "JOIN post p ON p.id = pi.post_id " +
            "WHERE pi.deleted = 0 " +
            "AND pi.main_image = 0 " +
            "AND p.created_by = ?2 " +
            "AND pi.id IN (?1)", nativeQuery = true)
    List<PostImageEntity> findAllByIdInAndCreatedByAndMainImageFalse(List<Long> ids, Long userId);
}
