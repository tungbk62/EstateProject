package com.example.datnbackend.dto.notification;

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
public class NotificationDetailForBusinessResponse {
    private Long id;
    private String message;
    private String typeNotification;
    private Boolean viewed;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
}
