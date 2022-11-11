package com.example.datnbackend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDescriptionPostDetailResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private Double ratingPoint;
    private String imageUrl;
    private String type;
}
