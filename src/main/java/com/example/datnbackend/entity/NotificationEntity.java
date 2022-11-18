package com.example.datnbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification")
@Entity
public class NotificationEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "message", nullable = false)
    private String message;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private TypeNotificationEntity typeNotification;

    @Column(name = "viewed", nullable = false)
    private Boolean viewed;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private UserEntity createdBy;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @PrePersist
    void prePersist(){
        createdDate = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    }
}
