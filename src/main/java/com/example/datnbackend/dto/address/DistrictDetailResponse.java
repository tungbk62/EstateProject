package com.example.datnbackend.dto.address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DistrictDetailResponse {
    private Long id;
    private String name;
    private List<WardsDetailResponse> wards;
}
