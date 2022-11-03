package com.example.datnbackend.service.impl;

import com.example.datnbackend.dto.address.DistrictDetailResponse;
import com.example.datnbackend.dto.address.ProvinceDetailResponse;
import com.example.datnbackend.dto.address.ProvinceResponse;
import com.example.datnbackend.dto.exception.AppException;
import com.example.datnbackend.entity.DistrictEntity;
import com.example.datnbackend.entity.ProvinceEntity;
import com.example.datnbackend.mapper.DistrictMapper;
import com.example.datnbackend.repository.ProvinceRepository;
import com.example.datnbackend.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {
    @Autowired
    ProvinceRepository provinceRepository;
    @Autowired
    DistrictMapper districtMapper;

    @Override
    public List<ProvinceResponse> getAllProvince() {
        List<ProvinceEntity> provinceEntityList = provinceRepository.findAll();
        if(provinceEntityList.isEmpty()){
            throw new AppException("Cannot get list of province");
        }
        List<ProvinceResponse> provinceResponseList = provinceEntityList.stream()
                .map(o -> new ProvinceResponse(o.getId(), o.getName())).collect(Collectors.toList());
        return provinceResponseList;
    }

    @Override
    public ProvinceDetailResponse getProvinceDetail(Long id) {
        ProvinceEntity provinceEntity = provinceRepository.findOneById(id);
        if(provinceEntity == null){
            throw new AppException("Cannot find province with id: " + id);
        }
        List<DistrictEntity> districtEntityList = provinceEntity.getDistrictList();
        List<DistrictDetailResponse> districtDetailResponseList;

        if(districtEntityList.isEmpty()){
            districtDetailResponseList = Collections.emptyList();
        }else {
            districtDetailResponseList = districtEntityList.stream()
                    .map(o -> districtMapper.convertEntityToDetailResponse(o)).collect(Collectors.toList());
        }

        return new ProvinceDetailResponse(provinceEntity.getId(), provinceEntity.getName(), districtDetailResponseList);
    }


}
