package com.example.datnbackend.service;

import com.example.datnbackend.dto.post.PostCreateRequest;
import com.example.datnbackend.dto.post.PostDescriptionResponse;
import com.example.datnbackend.dto.post.PostDetailResponse;

import java.util.List;

public interface PostService {
    PostDetailResponse createPost(PostCreateRequest requestBody);
    PostDetailResponse updatePost(Long id, PostCreateRequest requestBody);
    List<PostDescriptionResponse> getPostDescriptionList(Integer page, Integer size, String order, Long province, Long district,
                                                   Long wards, String address, List<Long> type, Integer room, Double pricemin,
                                                   Double pricemax);
    PostDetailResponse getPostDetail(Long id);
    void hidePost(Long id, Boolean hide);
    void deleteMultiplePost(List<Long> ids);
    void changeStateOfHideLockedVerified(Long id, Boolean hide, Boolean locked, Boolean verified);
    void savePostToUser(Long id);
}
