package com.example.datnbackend.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    void updateAvatarImage(Long id, MultipartFile file);
    String saveAvatarImageGetUrl(MultipartFile file);
}
