package com.example.datnbackend.service.impl;

import com.example.datnbackend.dto.exception.AppException;
import com.example.datnbackend.dto.type.TypeReportRequest;
import com.example.datnbackend.dto.type.TypeReportResponse;
import com.example.datnbackend.entity.TypeReportEntity;
import com.example.datnbackend.entity.UserEntity;
import com.example.datnbackend.repository.TypeReportRepository;
import com.example.datnbackend.repository.UserRepository;
import com.example.datnbackend.security.UserPrincipal;
import com.example.datnbackend.service.TypeReportService;
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
public class TypeReportServiceImpl implements TypeReportService {
    @Autowired
    TypeReportRepository typeReportRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public TypeReportResponse createTypeReport(TypeReportRequest requestBody) {
        if(requestBody.getName() == null || requestBody.getName().isEmpty()){
            throw new AppException("Tên loại báo cáo không được null hoặc trống");
        }
        TypeReportEntity typeReportEntity = new TypeReportEntity();
        typeReportEntity.setName(requestBody.getName());
        typeReportEntity.setCreatedBy(getCurrentUser());

        typeReportEntity = typeReportRepository.save(typeReportEntity);
        return new TypeReportResponse(typeReportEntity.getId(), typeReportEntity.getName());
    }

    @Override
    public List<TypeReportResponse> getAllTypeReport(Integer page, Integer size) {
        List<TypeReportEntity> typeReportEntityList;
        if(page != null && size != null){
            Pageable pageable = PageRequest.of(page, size);
            typeReportEntityList = typeReportRepository.findAll(pageable).toList();
        }else {
            typeReportEntityList = typeReportRepository.findAll();
        }
        if(typeReportEntityList.isEmpty()){
            return Collections.emptyList();
        }
        List<TypeReportResponse> typeReportResponseList = typeReportEntityList.stream()
                .map(o -> new TypeReportResponse(o.getId(), o.getName())).collect(Collectors.toList());
        return typeReportResponseList;
    }

    @Override
    public TypeReportResponse updateTypeReport(Long id, TypeReportRequest requestBody) {
        if(requestBody.getName() == null || requestBody.getName().isEmpty()){
            throw new AppException("Tên loại báo cáo không được null hoặc trống");
        }
        TypeReportEntity typeReportEntity = typeReportRepository.findById(id).get();
        if(typeReportEntity == null){
            throw new AppException("Không tìm thấy loại báo cáo");
        }
        typeReportEntity.setName(requestBody.getName());
        typeReportEntity.setModifiedBy(getCurrentUser());
        typeReportEntity = typeReportRepository.save(typeReportEntity);
        return new TypeReportResponse(typeReportEntity.getId(), typeReportEntity.getName());
    }

    @Override
    @Transactional
    public void deleteTypeReport(List<Long> ids) {
        List<TypeReportEntity> typeReportEntityList = typeReportRepository.findAllByIdIn(ids);
        if(typeReportEntityList.size() != ids.size()){
            throw new AppException("Có id không tồn tại");
        }

        typeReportRepository.deleteByIdIn(ids);
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
