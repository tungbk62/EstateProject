package com.example.datnbackend.mapper;

import com.example.datnbackend.dto.address.DistrictDetailResponse;
import com.example.datnbackend.dto.address.WardsDetailResponse;
import com.example.datnbackend.entity.DistrictEntity;
import com.example.datnbackend.entity.WardsEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DistrictMapper {
    public DistrictDetailResponse convertEntityToDetailResponse(DistrictEntity districtEntity){
        if(districtEntity == null){
            return null;
        }
        List<WardsEntity> wardsEntityList = districtEntity.getWardsList();
        List<WardsDetailResponse> wardsDetailResponseList;
        if(wardsEntityList.isEmpty()){
            wardsDetailResponseList = Collections.emptyList();
        }else {
            wardsDetailResponseList = wardsEntityList.stream()
                    .map(o -> new WardsDetailResponse(o.getId(), o.getName())).collect(Collectors.toList());
        }

        return new DistrictDetailResponse(districtEntity.getId(), districtEntity.getName(), wardsDetailResponseList);
    }
}
