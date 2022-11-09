package com.example.datnbackend.dto.contact;

import com.example.datnbackend.dto.post.PostDescriptionForAdminBusinessResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactDetailResponse {
    private Long id;
    private String emailContact;
    private String phoneContact;
    private String message;
    private Boolean viewed;
    private Boolean handled;
    private LocalDateTime createdDate;
}
