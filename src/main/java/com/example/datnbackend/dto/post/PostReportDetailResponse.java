package com.example.datnbackend.dto.post;

import com.example.datnbackend.dto.user.UserDescriptionPostDetailResponse;
import com.example.datnbackend.dto.user.UserDescriptionReviewResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostReportDetailResponse {
    private Long id;
    private PostDescriptionForAdminBusinessResponse postDescription;
    private String typeReportName;
    private String emailReport;
    private String phoneReport;
    private String description;
    private Boolean handled;
    private UserDescriptionReviewResponse handledBy;
}
