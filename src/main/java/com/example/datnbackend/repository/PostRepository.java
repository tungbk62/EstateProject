package com.example.datnbackend.repository;

import com.example.datnbackend.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    PostEntity findOneByIdAndDeletedFalseAndHideFalseAndLockedFalse(Long id);
}
