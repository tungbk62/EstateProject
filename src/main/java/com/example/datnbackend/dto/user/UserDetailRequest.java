package com.example.datnbackend.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailRequest {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthDay;
    private String phone;
    private String email;
    private Long wardsId;
    private String imageUrl;
    private Boolean displayReview;
    private Date createdDate;
}
