package com.example.datnbackend.dto.notification;

import com.example.datnbackend.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationCreateRequest {
    private List<Long> userIds;
    private String message;
    private Long typeNotificationId;
}
