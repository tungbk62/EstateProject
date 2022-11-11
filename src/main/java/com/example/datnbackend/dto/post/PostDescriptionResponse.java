package com.example.datnbackend.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
}
