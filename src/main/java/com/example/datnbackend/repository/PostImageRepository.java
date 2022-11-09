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
    PostImageEntity findAllByPostIdAndDeletedFalseAndMainImageFalse(Long postId);
}
