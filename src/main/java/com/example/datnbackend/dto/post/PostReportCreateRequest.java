package com.example.datnbackend.dto.post;

import com.example.datnbackend.dto.user.UserDescriptionPostDetailResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostReportCreateRequest {
    private Long typeReportId;
    private String emailReport;
    private String phoneReport;
    private String description;
}
