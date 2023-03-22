package com.example.datnbackend.service;

import com.example.datnbackend.dto.notification.*;
import com.example.datnbackend.dto.type.TypeNotificationResponse;

import java.util.List;

public interface NotificationService {
    List<TypeNotificationResponse> getAllTypeNotification();
    void createNotification(NotificationCreateRequest requestBody);
    NotificationDetailForAdminResponse getNotificationDetailForAdmin(Long id);
    NotificationDetailForBusinessResponse getNotificationDetailForBusiness(Long id);
    List<NotificationDescriptionForAdminResponse> getNotificationDescriptionForAdmin(Integer page, Integer size, Long userId, Long typeId, Boolean byMe);
    List<NotificationDescriptionForBusinessResponse> getNotificationDescriptionForBusiness(Integer page, Integer size, Long typeId, Boolean viewed);
    void deleteNotificationCreated(List<Long> ids);
}
