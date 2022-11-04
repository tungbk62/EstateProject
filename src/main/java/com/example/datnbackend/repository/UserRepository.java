package com.example.datnbackend.repository;

import com.example.datnbackend.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    @Query("SELECT u FROM UserEntity u WHERE u.username = ?1 AND u.deleted = FALSE")
    Optional<UserEntity> findByUsernameWithDeletedIsFalse(String username);
    Boolean existsByUsername(String username);
    UserEntity findByEmail(String email);
    UserEntity findByPhone(String phone);
    @Query("SELECT u FROM UserEntity u WHERE u.username = ?1 AND u.locked = FALSE")
    UserEntity findByUsernameWithLockedIsFalse(String username);
    @Query("SELECT u FROM UserEntity u WHERE u.id = ?1 AND u.deleted = FALSE")
    Optional<UserEntity> findByIdWithDeletedIsFalse(Long id);
    @Query(value = "SELECT u.* FROM user u " +
            "JOIN user_role ur ON u.id = ur.user_id " +
            "JOIN role r ON r.id = ur.role_id " +
            "WHERE (r.name = 'ROLE_BUSINESS' OR r.name = 'ROLE_CUSTOMER') " +
            "AND u.deleted = 0 " +
            "AND ((?1 IS NULL) OR (?1 = '') OR (u.username LIKE CONCAT('%',?1,'%')))"
            ,nativeQuery = true)
    List<UserEntity> findAllUserWithPagingAndQueryAndDeletedIsFalse(String query, Pageable pageable);
    @Query(value = "SELECT u.* FROM user u " +
            "JOIN user_role ur ON u.id = ur.user_id " +
            "JOIN role r ON r.id = ur.role_id " +
            "WHERE (r.name = 'ROLE_BUSINESS' OR r.name = 'ROLE_CUSTOMER') " +
            "AND u.deleted = 0 "
            ,nativeQuery = true)
    List<UserEntity> findAllUserWithDeletedIsFalseWithNormalRole();
    @Query(value = "SELECT u.* FROM user u " +
            "JOIN user_role ur ON u.id = ur.user_id " +
            "JOIN role r ON r.id = ur.role_id " +
            "WHERE (r.name = 'ROLE_BUSINESS' OR r.name = 'ROLE_CUSTOMER') " +
            "AND u.deleted = 0 " +
            "AND u.id = ?1"
            ,nativeQuery = true)
    UserEntity findOneByIdAndDeletedFalseWithNormalRole(Long id);
    UserEntity findOneByIdAndDeletedFalse(Long id);
    UserEntity findOneByIdAndPasswordAndDeletedFalse(Long id, String password);
}
