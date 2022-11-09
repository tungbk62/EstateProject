package com.example.datnbackend.dto.contact;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactDescriptionResponse {
    private Long id;
    private String emailContact;
    private String phoneContact;
    private Boolean viewed;
    private Boolean handled;
    private LocalDateTime createdDate;
}
