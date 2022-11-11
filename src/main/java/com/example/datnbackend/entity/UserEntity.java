package com.example.datnbackend.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
@Entity
public class UserEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "birthday")
    private LocalDate birthDay;

    @Column(name = "phone", unique = true)
    private String phone;

    @ManyToOne
    @JoinColumn(name = "wards_id")
    private WardsEntity wards;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "display_review", nullable = false)
    private Boolean displayReview;

    @Column(name = "locked", nullable = false)
    private Boolean locked;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles;

    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_save",
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "post_id", nullable = false)
    )
    private List<PostEntity> postSave;

    @PrePersist
    public void prePersist() {
        createdDate = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    }


}
