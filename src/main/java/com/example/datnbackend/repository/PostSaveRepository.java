package com.example.datnbackend.repository;

import com.example.datnbackend.entity.PostSaveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PostSaveRepository extends JpaRepository<PostSaveEntity, Long> {
    @Query(value = "SELECT ps.* FROM post_save ps " +
            "WHERE ps.user_id = ?1 AND ps.post_id IN (?2)", nativeQuery = true)
    List<PostSaveEntity> findAllByUserIdAndPostIdIn(Long userId, List<Long> ids);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM post_save ps WHERE ps.user_id = ?1 AND ps.post_id IN (?2)", nativeQuery = true)
    void deleteByUserIdAndPostIdIn(Long userId, List<Long> ids);
}
