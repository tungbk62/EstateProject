package com.example.datnbackend.dto.notification;

import com.example.datnbackend.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationCreateRequest {
    private Long userId;
    private String message;
    private Long typeNotificationId;
}
