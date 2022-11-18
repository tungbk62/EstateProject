package com.example.datnbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post")
@Entity
public class PostEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne
    @JoinColumn(name = "type_estate_id")
    private TypeEstateEntity typeEstate;

    @ManyToOne
    @JoinColumn(name = "wards_id")
    private WardsEntity wards;

    @Column(name = "address_detail")
    private String addressDetail;

    @Column(name = "area")
    private Double area;

    @Column(name = "price_month")
    private Double priceMonth;

    @Column(name = "furniture")
    private String furniture;

    @Column(name = "room")
    private Integer room;

    @Column(name = "bath_room")
    private Integer bathRoom;

    @Column(name = "expired_date")
    private LocalDateTime expiredDate;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    @Column(name = "hide", nullable = false)
    private Boolean hide;

    @Column(name = "locked", nullable = false)
    private Boolean locked;

    @Column(name = "verified", nullable = false)
    private Boolean verified;

    @Column(name = "view", nullable = false)
    private Integer view;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private UserEntity createdBy;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @ManyToOne
    @JoinColumn(name = "type_post_id", nullable = false)
    private TypePostEntity typePost;

    @PrePersist
    void prePersist(){
        createdDate = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    }
}
