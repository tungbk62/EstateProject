package com.example.datnbackend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDescriptionAdminResponse {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private Boolean displayReview;
    private Boolean locked;
}
