package com.example.datnbackend.service;

import com.example.datnbackend.dto.address.ProvinceDetailResponse;
import com.example.datnbackend.dto.address.ProvinceResponse;

import java.util.List;

public interface AddressService {
    List<ProvinceResponse> getAllProvince();
    ProvinceDetailResponse getProvinceDetail(Long id);
}
