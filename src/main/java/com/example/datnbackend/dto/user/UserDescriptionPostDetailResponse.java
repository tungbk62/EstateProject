package com.example.datnbackend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDescriptionPostDetailResponse {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private Double ratingPoint;
    private String imageUrl;
}
