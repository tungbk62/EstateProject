package com.example.datnbackend.dto.contact;

import com.example.datnbackend.dto.post.PostDescriptionForAdminBusinessResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
}
