package com.example.datnbackend.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCreateRequest {
    private String title;
    private String description;
    private Long typeEstateId;
    private Long wardsId;
    private String addressDetail;
    private Double area;
    private Double priceMonth;
    private String furniture;
    private Integer room;
    private Integer bathRoom;
    private Boolean hide;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime expiredDate;
    private Long typePostId;
}
