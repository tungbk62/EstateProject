package com.example.datnbackend.dto.notification;

import com.example.datnbackend.dto.user.UserDescriptionNotificationResponse;
import com.example.datnbackend.entity.TypeNotificationEntity;
import com.example.datnbackend.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDetailForAdminResponse {
    private Long id;
    private UserDescriptionNotificationResponse user;
    private String message;
    private String typeNotification;
    private Boolean viewed;
    private UserDescriptionNotificationResponse createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
}
