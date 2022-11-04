package com.example.datnbackend.service;

import com.example.datnbackend.dto.type.TypeReportRequest;
import com.example.datnbackend.dto.type.TypeReportResponse;

import java.util.List;

public interface TypeReportService {
    TypeReportResponse createTypeReport(TypeReportRequest requestBody);
    List<TypeReportResponse> getAllTypeReport(Integer page, Integer size);
    TypeReportResponse updateTypeReport(Long id, TypeReportRequest requestBody);
    void deleteTypeReport(List<Long> ids);
}
