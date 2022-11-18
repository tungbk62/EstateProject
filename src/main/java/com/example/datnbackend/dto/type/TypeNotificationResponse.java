package com.example.datnbackend.dto.type;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypeNotificationResponse {
    private Long id;
    private String name;
}
