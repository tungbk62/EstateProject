package com.example.datnbackend.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDescriptionForAdminBusinessResponse {
    private Long id;
    private String title;
    private String typeEstate;
    private String province;
    private String district;
    private String wards;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime expiredDate;
    private Boolean deleted;
    private Boolean hide;
    private Boolean locked;
    private Boolean verified;
    private String createdBy;
    private String mainImageUrl;
    private LocalDateTime createdDate;
}
