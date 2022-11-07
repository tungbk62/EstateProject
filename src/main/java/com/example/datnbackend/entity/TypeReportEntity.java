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
@Table(name = "type_report")
@Entity
public class TypeReportEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private UserEntity createdBy;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modified_by")
    private UserEntity modifiedBy;

    @PrePersist
    void prePersist(){
        createdDate = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    }

    @PreUpdate
    void preUpdate(){
        createdDate = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    }
}
