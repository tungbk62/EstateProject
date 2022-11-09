package com.example.datnbackend.dto.post;

import com.example.datnbackend.dto.user.UserDescriptionPostDetailResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostReportDescriptionResponse {
    private Long id;
    private String typeReportName;
    private String emailReport;
    private String phoneReport;
    private Boolean viewed;
    private Boolean handled;
}
