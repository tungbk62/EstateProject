package com.example.datnbackend.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDescriptionResponse {
    private Long id;
    private String title;
    private String typeEstate;
    private String province;
    private String district;
    private String wards;
    private Double area;
    private Double priceMonth;
    private Boolean verified;
    private String createdBy;
    private Integer imageNumber;
    private String mainImageUrl;
    private LocalDateTime createdDate;
}
