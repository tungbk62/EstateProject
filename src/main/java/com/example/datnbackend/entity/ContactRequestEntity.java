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
@Table(name = "contact_request")
@Entity
public class ContactRequestEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_business_id", nullable = false)
    private UserEntity userBusiness;

    @Column(name = "email_contact")
    private String emailContact;

    @Column(name = "phone_contact")
    private String phoneContact;

    @Column(name = "description")
    private String description;

    @Column(name = "business_viewed", nullable = false)
    private Boolean businessViewed;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @PrePersist
    void prePersist(){
        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
        createdDate = ldt;
    }
}
