package com.example.datnbackend.service;

import com.example.datnbackend.dto.review.ReviewCreateRequest;
import com.example.datnbackend.dto.review.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(Long id, ReviewCreateRequest requestBody);
    ReviewResponse updateReview(Long id, ReviewCreateRequest requestBody);
    List<ReviewResponse> getReviewList(Integer page, Integer size, String order, Long id);
}
