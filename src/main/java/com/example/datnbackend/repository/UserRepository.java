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
    @Query("SELECT u FROM UserEntity u WHERE u.email = ?1 AND u.deleted = FALSE")
    Optional<UserEntity> findByEmailWithDeletedFalse(String email);
    UserEntity findByEmailAndDeletedFalse(String email);
    UserEntity findByPhoneAndDeletedFalse(String phone);
    @Query("SELECT u FROM UserEntity u WHERE u.email = ?1 AND u.locked = FALSE")
    UserEntity findByEmailWithLockedIsFalse(String email);
    @Query("SELECT u FROM UserEntity u WHERE u.id = ?1 AND u.deleted = FALSE AND u.locked = FALSE")
    Optional<UserEntity> findByIdWithDeletedFalseAndLockedFalse(Long id);
    @Query(value = "SELECT u.* FROM user u " +
            "JOIN user_role ur ON u.id = ur.user_id " +
            "JOIN role r ON r.id = ur.role_id " +
            "WHERE (r.name = 'ROLE_BUSINESS' OR r.name = 'ROLE_CUSTOMER') " +
            "AND u.deleted = 0 " +
            "AND ((?1 IS NULL) OR (r.name = ?1))" +
            "AND ((?2 IS NULL) OR (?2 = '') OR (u.email LIKE CONCAT('%',?2,'%')) " +
            "OR (u.first_name LIKE CONCAT('%',?2,'%')) OR (u.last_name LIKE CONCAT('%',?2,'%')))"
            ,nativeQuery = true)
    List<UserEntity> findAllUserWithPagingAndQueryAndDeletedIsFalse(String type, String query, Pageable pageable);
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
    @Query(value = "SELECT u.* FROM user u " +
            "JOIN user_role ur ON u.id = ur.user_id " +
            "JOIN role r ON r.id = ur.role_id " +
            "WHERE r.name = 'ROLE_BUSINESS' " +
            "AND u.deleted = 0 " +
            "AND u.id = ?1"
            ,nativeQuery = true)
    UserEntity findOneByIdAndDeletedFalseWithRoleBusiness(Long id);
    UserEntity findOneByIdAndDeletedFalse(Long id);
    UserEntity findOneByIdAndDeletedFalseAndLockedFalse(Long id);
    UserEntity findOneByEmailAndDeletedFalseAndLockedFalse(String email);
}
