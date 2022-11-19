package com.example.datnbackend.service.impl;

import com.example.datnbackend.dto.exception.AppException;
import com.example.datnbackend.dto.notification.*;
import com.example.datnbackend.dto.type.TypeNotificationResponse;
import com.example.datnbackend.dto.user.UserDescriptionNotificationResponse;
import com.example.datnbackend.entity.NotificationEntity;
import com.example.datnbackend.entity.TypeNotificationEntity;
import com.example.datnbackend.entity.UserEntity;
import com.example.datnbackend.repository.NotificationRepository;
import com.example.datnbackend.repository.TypeNotificationRepository;
import com.example.datnbackend.repository.UserRepository;
import com.example.datnbackend.security.UserPrincipal;
import com.example.datnbackend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    TypeNotificationRepository typeNotificationRepository;
    @Value("${image.avatar.default}")
    private String avatarDefaultUrl;


    @Override
    public List<TypeNotificationResponse> getAllTypeNotification() {
        List<TypeNotificationEntity> typeNotificationEntityList = typeNotificationRepository.findAll();
        if(typeNotificationEntityList.isEmpty()){
            return Collections.emptyList();
        }
        return typeNotificationEntityList.stream().map(o -> new TypeNotificationResponse(o.getId(), o.getName())).collect(Collectors.toList());
    }

    @Override
    public NotificationDetailForAdminResponse createNotification(NotificationCreateRequest requestBody) {
        if(requestBody.getTypeNotificationId() == null || requestBody.getUserId() == null || requestBody.getMessage() == null){
            throw new AppException("Không được null");
        }

        UserEntity normalUserEntity = userRepository.findOneByIdAndDeletedFalseWithNormalRole(requestBody.getUserId());
        if(normalUserEntity == null){
            throw new AppException("Không tìm thấy user thường");
        }

        TypeNotificationEntity typeNotificationEntity = typeNotificationRepository.findOneById(requestBody.getTypeNotificationId());
        if(typeNotificationEntity == null){
            throw new AppException("Không tìm thấy kiểu thông báo");
        }

        UserEntity currentUser = getCurrentUserEntity();

        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setUser(normalUserEntity);
        notificationEntity.setMessage(requestBody.getMessage());
        notificationEntity.setCreatedBy(currentUser);
        notificationEntity.setDeleted(false);
        notificationEntity.setViewed(false);
        notificationEntity.setTypeNotification(typeNotificationEntity);

        notificationEntity = notificationRepository.save(notificationEntity);
        return new NotificationDetailForAdminResponse(notificationEntity.getId(),
                convertEntityToUserDescriptionNotificationResponse(notificationEntity.getUser()), notificationEntity.getMessage(),
                notificationEntity.getTypeNotification().getName(), notificationEntity.getViewed(),
                convertEntityToUserDescriptionNotificationResponse(notificationEntity.getCreatedBy()), notificationEntity.getCreatedDate());
    }

    @Override
    public NotificationDetailForAdminResponse getNotificationDetailForAdmin(Long id) {
        NotificationEntity notificationEntity = notificationRepository.findOneByIdAndDeletedFalse(id);
        if(notificationEntity == null){
            throw new AppException("Không tìm thấy thông báo");
        }
        return new NotificationDetailForAdminResponse(notificationEntity.getId(),
                convertEntityToUserDescriptionNotificationResponse(notificationEntity.getUser()), notificationEntity.getMessage(),
                notificationEntity.getTypeNotification().getName(), notificationEntity.getViewed(),
                convertEntityToUserDescriptionNotificationResponse(notificationEntity.getCreatedBy()), notificationEntity.getCreatedDate());
    }

    @Override
    public NotificationDetailForBusinessResponse getNotificationDetailForBusiness(Long id) {
        UserEntity currentUser = getCurrentUserEntity();
        NotificationEntity notificationEntity = notificationRepository.findOneByIdAndUserIdAndDeletedFalse(id, currentUser.getId());
        if(notificationEntity == null){
            throw new AppException("Không tìm thấy thông báo");
        }

        if(!notificationEntity.getViewed()){
            notificationEntity.setViewed(true);
            notificationRepository.save(notificationEntity);
        }

        return new NotificationDetailForBusinessResponse(notificationEntity.getId(), notificationEntity.getMessage(),
                notificationEntity.getTypeNotification().getName(), notificationEntity.getViewed(), notificationEntity.getCreatedDate());
    }

    @Override
    public List<NotificationDescriptionForAdminResponse> getNotificationDescriptionForAdmin(Integer page, Integer size, Long userId, Long typeId, Boolean byMe) {
        UserEntity currentUser = getCurrentUserEntity();
        Pageable pageable = PageRequest.of(page, size);
        List<NotificationEntity> notificationEntityList;
        if(byMe == null || !byMe){
            notificationEntityList = notificationRepository.findAllByCreatedByAndDeletedFalse(userId, null, typeId, pageable);
        }else {
            notificationEntityList = notificationRepository.findAllByCreatedByAndDeletedFalse(userId, currentUser.getId(), typeId, pageable);
        }

        if(notificationEntityList.isEmpty()){
            return Collections.emptyList();
        }

        return notificationEntityList.stream().map(o -> new NotificationDescriptionForAdminResponse(
                o.getId(), convertEntityToUserDescriptionNotificationResponse(o.getUser()),
                o.getTypeNotification().getName(), o.getViewed(), o.getCreatedDate())).collect(Collectors.toList());
    }

    @Override
    public List<NotificationDescriptionForBusinessResponse> getNotificationDescriptionForBusiness(Integer page, Integer size, Long typeId, Boolean viewed) {
        UserEntity currentUser = getCurrentUserEntity();
        Pageable pageable = PageRequest.of(page, size);
        Integer viewedInt;
        if(viewed != null){
            viewedInt = viewed ? 1 : 0;
        }else {
            viewedInt = null;
        }

        List<NotificationEntity> notificationEntityList = notificationRepository.findAllByUserAndDeletedFalse(currentUser.getId(), typeId, viewedInt, pageable);

        if(notificationEntityList.isEmpty()){
            return Collections.emptyList();
        }

        return notificationEntityList.stream().map(o -> new NotificationDescriptionForBusinessResponse(
                o.getId(), o.getTypeNotification().getName(), o.getViewed(), o.getCreatedDate()
        )).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteNotificationCreated(List<Long> ids) {
        if(ids == null || ids.isEmpty()){
            throw new AppException("Phải khác null");
        }

        UserEntity currentUser = getCurrentUserEntity();

        List<NotificationEntity> notificationEntityList = notificationRepository.findAllByIdInAndCreatedByAndDeletedFalse(ids, currentUser.getId());
        if(notificationEntityList.size() != ids.size()){
            throw new AppException("Không thành công");
        }

        for(NotificationEntity i : notificationEntityList){
            i.setDeleted(true);
            notificationRepository.save(i);
        }

    }


    private UserEntity getCurrentUserEntity(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity userEntity = userRepository.findOneByIdAndDeletedFalseAndLockedFalse(userPrincipal.getId());
        if(userEntity == null){
            throw new AppException("Not found current user");
        }
        return userEntity;
    }

    private UserDescriptionNotificationResponse convertEntityToUserDescriptionNotificationResponse(UserEntity userEntity){
        if(userEntity == null){
            return null;
        }
        return new UserDescriptionNotificationResponse(userEntity.getId(), userEntity.getEmail(), userEntity.getFirstName(),
                userEntity.getLastName(), userEntity.getImageUrl() == null ? avatarDefaultUrl : userEntity.getEmail(),
                returnTypeOfUser(userEntity));
    }

    private String returnTypeOfUser(UserEntity userEntity){
        if(userEntity == null){
            return null;
        }
        String role = userEntity.getRoles().stream().findFirst().get().getName().toString();
        if(role.equalsIgnoreCase("ROLE_BUSINESS")){
            return "BUSINESS";
        }else if(role.equalsIgnoreCase("ROLE_CUSTOMER")){
            return  "CUSTOMER";
        }else if(role.equalsIgnoreCase("ROLE_ADMIN")){
            return "ADMIN";
        }else {
            return null;
        }
    }
}
