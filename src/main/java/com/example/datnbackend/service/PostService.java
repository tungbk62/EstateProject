package com.example.datnbackend.service;

import com.example.datnbackend.dto.post.*;

import java.util.List;

public interface PostService {
    PostDetailForBusinessResponse createPost(PostCreateRequest requestBody);
    PostDetailForBusinessResponse updatePost(Long id, PostCreateRequest requestBody);
    List<PostDescriptionResponse> getPostDescriptionList(Integer page, Integer size, String order, Long province, Long district,
                                                         Long wards, List<Long> type, Integer room, Double pricemin, Double pricemax,
                                                         Double areamin, Double areamax);
    List<PostDescriptionResponse> getPostDescriptionListSearch(Integer page, Integer size, String order, String search);
    List<PostDescriptionForAdminBusinessResponse> getPostDescriptionListForBusiness(Integer page, Integer size);
    List<PostDescriptionForAdminBusinessResponse> getPostDescriptionListForAdmin(Integer page, Integer size);
    PostDetailResponse getPostDetail(Long id);
    PostDetailForAdminResponse getPostDetailForAdmin(Long id);
    PostDetailForBusinessResponse getPostDetailForBusiness(Long id);
    void hidePost(Long id, Boolean hide);
    void deleteMultiplePost(List<Long> ids);
    void changeStateOfHideLockedVerified(Long id, Boolean hide, Boolean locked, Boolean verified);
    void savePostToUser(Long id);
    List<PostDescriptionResponse> getDescriptionPostListSave(Integer page, Integer size);
}
