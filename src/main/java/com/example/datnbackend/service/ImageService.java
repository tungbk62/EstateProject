package com.example.datnbackend.service;

import com.example.datnbackend.dto.post.PostImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
    void updateAvatarImage(MultipartFile file);
    void uploadPostImage(Long id, List<MultipartFile> files, Integer main);
    List<PostImageResponse> getPostImage(Long id);
    void changeMainImage(Long id);
    void deleteImage(List<Long> ids);
}
