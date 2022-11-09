package com.example.datnbackend.dto.review;

import com.example.datnbackend.dto.user.UserDescriptionReviewResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private Long id;
    private String description;
    private Integer ratingPoint;
    private UserDescriptionReviewResponse createdBy;
    private LocalDateTime createdDate;
}
