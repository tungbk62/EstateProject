package com.example.datnbackend.service;

import com.example.datnbackend.dto.type.TypeEstateRequest;
import com.example.datnbackend.dto.type.TypeEstateResponse;

import java.util.List;

public interface TypeEstateService {
    TypeEstateResponse createTypeEstate(TypeEstateRequest requestBody);
    List<TypeEstateResponse> getAllTypeEstate(Integer page, Integer size);
    TypeEstateResponse updateTypeEstate(Long id, TypeEstateRequest requestBody);
    void deleteTypeEstate(List<Long> ids);
}
