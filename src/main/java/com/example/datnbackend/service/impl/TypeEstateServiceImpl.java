package com.example.datnbackend.service.impl;

import com.example.datnbackend.dto.exception.AppException;
import com.example.datnbackend.dto.type.TypeEstateRequest;
import com.example.datnbackend.dto.type.TypeEstateResponse;
import com.example.datnbackend.entity.TypeEstateEntity;
import com.example.datnbackend.entity.UserEntity;
import com.example.datnbackend.repository.TypeEstateRepository;
import com.example.datnbackend.repository.UserRepository;
import com.example.datnbackend.security.UserPrincipal;
import com.example.datnbackend.service.TypeEstateService;
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
public class TypeEstateServiceImpl implements TypeEstateService {
    @Autowired
    TypeEstateRepository typeEstateRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public TypeEstateResponse createTypeEstate(TypeEstateRequest requestBody) {
        if(requestBody.getName() == null || requestBody.getName().isEmpty()){
            throw new AppException("Tên kiểu bất động sản không được null hoặc trống");
        }
        TypeEstateEntity typeEstateEntity = new TypeEstateEntity();
        typeEstateEntity.setName(requestBody.getName());
        typeEstateEntity.setCreatedBy(getCurrentUser());

        typeEstateEntity = typeEstateRepository.save(typeEstateEntity);
        return new TypeEstateResponse(typeEstateEntity.getId(), typeEstateEntity.getName());
    }

    @Override
    public List<TypeEstateResponse> getAllTypeEstate(Integer page, Integer size) {
        List<TypeEstateEntity> typeEstateEntityList;
        if(page != null && size != null){
            Pageable pageable = PageRequest.of(page, size);
            typeEstateEntityList = typeEstateRepository.findAll(pageable).toList();
        }else {
            typeEstateEntityList = typeEstateRepository.findAll();
        }
        if(typeEstateEntityList.isEmpty()){
            return Collections.emptyList();
        }
        List<TypeEstateResponse> typeEstateResponseList = typeEstateEntityList.stream()
                .map(o -> new TypeEstateResponse(o.getId(), o.getName())).collect(Collectors.toList());
        return typeEstateResponseList;
    }

    @Override
    public TypeEstateResponse updateTypeEstate(Long id, TypeEstateRequest requestBody) {
        if(requestBody.getName() == null || requestBody.getName().isEmpty()){
            throw new AppException("Tên kiểu bất động sản không được null hoặc trống");
        }
        TypeEstateEntity typeEstateEntity = typeEstateRepository.findById(id).get();
        if(typeEstateEntity == null){
            throw new AppException("Không tìm thấy kiểu bất động sản");
        }
        typeEstateEntity.setName(requestBody.getName());
        typeEstateEntity.setModifiedBy(getCurrentUser());
        typeEstateEntity = typeEstateRepository.save(typeEstateEntity);
        return new TypeEstateResponse(typeEstateEntity.getId(), typeEstateEntity.getName());
    }

    @Override
    @Transactional
    public void deleteTypeEstate(List<Long> ids) {
        List<TypeEstateEntity> typeEstateEntityList = typeEstateRepository.findAllByIdIn(ids);
        if(typeEstateEntityList.size() != ids.size()){
            throw new AppException("Có id không tồn tại");
        }

        typeEstateRepository.deleteByIdIn(ids);
    }

    private UserEntity getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity userEntity = userRepository.findOneByIdAndDeletedFalseAndLockedFalse(userPrincipal.getId());
        if(userEntity == null){
            throw new AppException("Not found current user");
        }
        return userEntity;
    }
}
