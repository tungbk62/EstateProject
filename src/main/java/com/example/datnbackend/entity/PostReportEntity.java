package com.example.datnbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post_report")
@Entity
public class PostReportEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @ManyToOne
    @JoinColumn(name = "type_report_id")
    private TypeReportEntity typeReport;

    @Column(name = "email_report")
    private String emailReport;

    @Column(name = "phone_report")
    private String phoneReport;

    @Column(name = "description")
    private String description;

    @Column(name = "admin_viewed", nullable = false)
    private Boolean adminViewed;

    @Column(name = "admin_handled", nullable = false)
    private Boolean adminHandled;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    @Column(name = "created_date", nullable = false)
    private Date createdDate;

    @PrePersist
    void prePersist(){
        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
        createdDate = Timestamp.valueOf(ldt);
    }
}
