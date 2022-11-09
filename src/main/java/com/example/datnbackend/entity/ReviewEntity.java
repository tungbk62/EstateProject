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
@Table(name = "review")
@Entity
public class ReviewEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_business_id", nullable = false)
    private UserEntity userBusiness;

    @Column(name = "description")
    private String description;

    @Column(name = "rating_point", nullable = false)
    private Integer ratingPoint;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private UserEntity createdBy;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @PrePersist
    void prePersist(){
        createdDate = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    }

    @PreUpdate
    void preUpdate(){
        createdDate = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    }
}
