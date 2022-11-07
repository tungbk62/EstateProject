package com.example.datnbackend.dto.post;

import com.example.datnbackend.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDescriptionAdminResponse {
    private Long id;
    private String title;
    private String description;
    private String typeEstate;
    private String province;
    private String district;
    private String wards;
    private String addressDetail;
    private Double area;
    private Double priceMonth;
    private String furniture;
    private Integer room;
    private Integer bathRoom;
    private Boolean deleted;
    private Boolean hide;
    private Boolean locked;
    private Boolean verified;
    private Integer view;
    private UserEntity createdBy;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
}
