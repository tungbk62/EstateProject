package com.example.datnbackend.dto.contact;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactCreateRequest {
    private String emailContact;
    private String phoneContact;
    private String message;
}
