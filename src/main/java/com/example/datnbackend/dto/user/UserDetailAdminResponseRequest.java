package com.example.datnbackend.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailAdminResponseRequest {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthDay;
    private String phone;
    private String province;
    private String district;
    private String wards;
    private String imageUrl;
    private Boolean displayReview;
    private Boolean locked;
    private Boolean deleted;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
    private String type;
}
