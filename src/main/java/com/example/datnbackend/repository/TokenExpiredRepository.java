package com.example.datnbackend.repository;

import com.example.datnbackend.entity.TokenExpiredEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenExpiredRepository extends JpaRepository<TokenExpiredEntity, Long> {
    TokenExpiredEntity findOneByToken(String token);
}
