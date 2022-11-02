package com.example.datnbackend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailAdminResponseRequest {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private Date birthDay;
    private String phone;
    private String email;
    private String address;
    private String imageUrl;
    private Boolean displayReview;
    private Boolean locked;
    private Date createdDate;
}
