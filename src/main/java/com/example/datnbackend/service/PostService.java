package com.example.datnbackend.service;

import com.example.datnbackend.dto.post.*;
import com.example.datnbackend.dto.type.TypePostResponse;

import java.util.List;

public interface PostService {
    PostDetailForBusinessResponse createPost(PostCreateRequest requestBody);
    PostDetailForBusinessResponse updatePost(Long id, PostCreateRequest requestBody);
    List<PostDescriptionResponse> getPostDescriptionList(Integer page, Integer size, String order, String search, Long province, Long district,
                                                         Long wards, List<Long> type, Integer room, Double pricemin, Double pricemax,
                                                         Double areamin, Double areamax);
    List<PostDescriptionForAdminBusinessResponse> getPostDescriptionListForBusiness(Integer page, Integer size, Long typePostId);
    List<PostDescriptionForAdminBusinessResponse> getPostDescriptionListForAdmin(Integer page, Integer size, Long userId, Long typePostId);
    PostDetailResponse getPostDetail(Long id);
    PostDetailForAdminResponse getPostDetailForAdmin(Long id);
    PostDetailForBusinessResponse getPostDetailForBusiness(Long id);
    void hidePost(Long id, Boolean hide);
    void deleteMultiplePost(List<Long> ids);
    void changeStateOfHideLockedVerified(Long id, Boolean hide, Boolean locked, Boolean verified);
    void savePostToUser(Long id);
    List<PostDescriptionResponse> getDescriptionPostListSave(Integer page, Integer size);
    void deletePostSave(List<Long> ids);
    PostReportDetailResponse createPostReport(Long id, PostReportCreateRequest requestBody);
    List<PostReportDescriptionResponse> getPostReportList(Integer page, Integer size, String order, Long postId, Long typeId, Long userId, Boolean handled);
    PostReportDetailResponse getPostReportDetail(Long id);
    void changeHandledState(Long id, Boolean handled);
    void deletePostReport(List<Long> ids);
    List<TypePostResponse> getAllTypePost();
    void changeTypePost(Long id, Long typePostId);
}
