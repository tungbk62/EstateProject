package com.example.datnbackend.service.impl;

import com.example.datnbackend.dto.contact.ContactCreateRequest;
import com.example.datnbackend.dto.contact.ContactDescriptionResponse;
import com.example.datnbackend.dto.contact.ContactDetailResponse;
import com.example.datnbackend.dto.exception.AppException;
import com.example.datnbackend.entity.ContactRequestEntity;
import com.example.datnbackend.entity.UserEntity;
import com.example.datnbackend.repository.ContactRequestRepository;
import com.example.datnbackend.repository.UserRepository;
import com.example.datnbackend.security.UserPrincipal;
import com.example.datnbackend.service.ContactService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ContactServiceImpl implements ContactService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ContactRequestRepository contactRequestRepository;

    @Override
    public ContactDetailResponse createContact(Long id, ContactCreateRequest requestBody) {
        UserEntity userEntity = userRepository.findOneByIdAndDeletedFalseWithRoleBusiness(id);
        if(userEntity == null){
            throw new AppException("Không tìm thấy tài khoản business với id: " + id);
        }

        if(requestBody.getEmailContact() == null && requestBody.getPhoneContact() == null){
            throw new AppException("Cần điền thông tin email hoặc số điện thoại");
        }

        ContactRequestEntity contactRequestEntity = new ContactRequestEntity();
        contactRequestEntity.setUserBusiness(userEntity);
        contactRequestEntity.setEmailContact(requestBody.getEmailContact());
        contactRequestEntity.setPhoneContact(requestBody.getPhoneContact());
        contactRequestEntity.setMessage(requestBody.getMessage());
        contactRequestEntity.setViewed(false);
        contactRequestEntity.setHandled(false);
        contactRequestEntity.setDeleted(false);

        contactRequestEntity = contactRequestRepository.save(contactRequestEntity);

        return new ContactDetailResponse(contactRequestEntity.getId(), contactRequestEntity.getEmailContact(), contactRequestEntity.getPhoneContact(),
                contactRequestEntity.getMessage(), contactRequestEntity.getViewed(), contactRequestEntity.getHandled(),
                contactRequestEntity.getCreatedDate());
    }

    @Override
    public List<ContactDescriptionResponse> getContactList(Integer page, Integer size, String order, Boolean viewed, Boolean handled) {
        if(order != null && order.equalsIgnoreCase("DATEDESC")){
            order = order.toUpperCase();
        }else{
            order = null;
        }
        UserEntity currentUserEntity = getCurrentUserEntity();

        Pageable pageable = PageRequest.of(page, size);
        Integer viewedInt, handledInt;

        if(viewed == null){
            viewedInt = null;
        }else {
            viewedInt = viewed ? 1 : 0;
        }

        if(handled == null){
            handledInt = null;
        }else {
            handledInt = handled ? 1 : 0;
        }

        List<ContactRequestEntity> contactRequestEntityList
                = contactRequestRepository.findAllByUserBusinessIdAndFilter(currentUserEntity.getId(), order, viewedInt, handledInt, pageable);
        if(contactRequestEntityList.isEmpty()){
            return Collections.emptyList();
        }
        return contactRequestEntityList.stream()
                .map(o -> new ContactDescriptionResponse(o.getId(), o.getEmailContact(), o.getPhoneContact(), o.getViewed(),
                        o.getHandled(), o.getCreatedDate())).collect(Collectors.toList());
    }

    @Override
    public ContactDetailResponse getContactDetail(Long id) {
        ContactRequestEntity contactRequestEntity = contactRequestRepository.findOneByIdAndDeletedFalse(id);
        if(contactRequestEntity == null){
            throw new AppException("Không tìm thấy yêu cầu liên hệ");
        }
        UserEntity currentUserEntity = getCurrentUserEntity();
        if(currentUserEntity.getId() != contactRequestEntity.getUserBusiness().getId()){
            throw new AppException("Không có quyền truy cập");
        }

        contactRequestEntity.setViewed(true);
        contactRequestEntity = contactRequestRepository.save(contactRequestEntity);

        return new ContactDetailResponse(contactRequestEntity.getId(), contactRequestEntity.getEmailContact(), contactRequestEntity.getPhoneContact(),
                contactRequestEntity.getMessage(), contactRequestEntity.getViewed(), contactRequestEntity.getHandled(),
                contactRequestEntity.getCreatedDate());
    }

    @Override
    public void changeStateHandled(Long id, Boolean handled) {
        ContactRequestEntity contactRequestEntity = contactRequestRepository.findOneByIdAndDeletedFalse(id);
        if(contactRequestEntity == null){
            throw new AppException("Không tìm thấy yêu cầu liên hệ");
        }
        UserEntity currentUserEntity = getCurrentUserEntity();
        if(currentUserEntity.getId() != contactRequestEntity.getUserBusiness().getId()){
            throw new AppException("Không có quyền truy cập");
        }

        contactRequestEntity.setHandled(handled);
        contactRequestRepository.save(contactRequestEntity);
    }

    @Override
    @Transactional
    public void deleteContactRequest(List<Long> ids) {
        if(ids == null || ids.isEmpty()){
            throw new AppException("Xoá không thành công");
        }
        UserEntity currentUser = getCurrentUserEntity();

        List<ContactRequestEntity> contactRequestEntityList
                = contactRequestRepository.findAllByIdInAndUserBusinessIdAndDeletedFalse(ids, currentUser.getId());

        if(contactRequestEntityList.size() != ids.size()){
            throw new AppException("Xoá không thành công");
        }

        for(ContactRequestEntity i : contactRequestEntityList){
            i.setDeleted(true);
            contactRequestRepository.save(i);
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

}
