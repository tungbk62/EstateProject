package com.example.datnbackend.service.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.datnbackend.dto.exception.AppException;
import com.example.datnbackend.entity.UserEntity;
import com.example.datnbackend.repository.UserRepository;
import com.example.datnbackend.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.apache.http.entity.ContentType.*;

@Service
public class ImageServiceImpl implements ImageService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    AmazonS3 amazonS3;

    private final String bucketName = "datnbucket1";
    @Override
    public void updateAvatarImage(Long id, MultipartFile file) {
        if(file == null || file.isEmpty()){
            throw new IllegalStateException("File trống");
        }

        UserEntity userEntity = userRepository.findOneByIdAndDeletedFalseAndLockedFalse(id);
        if(userEntity == null){
            throw new AppException("Không tìm thấy user");
        }

        checkImageFile(file);
        Map<String, String> metadata = extractMetadata(file);

        String filename = String.format("%s/%s", "avatar", UUID.randomUUID().toString());
        String imageUrl;

        try {
            imageUrl = saveImageToS3(bucketName, filename, Optional.of(metadata), file);
            userEntity.setImageUrl(imageUrl);
            userRepository.save(userEntity);
        }catch (Exception e){
            throw new AppException(e.getMessage());
        }

    }

    @Override
    public String saveAvatarImageGetUrl(MultipartFile file) {
        if(file == null || file.isEmpty()){
            throw new IllegalStateException("File trống");
        }
        checkImageFile(file);
        Map<String, String> metadata = extractMetadata(file);

        String filename = String.format("%s/%s", "avatar", UUID.randomUUID().toString());

        try {
            return saveImageToS3(bucketName, filename, Optional.of(metadata), file);
        }catch (Exception e){
            throw new AppException(e.getMessage());
        }
    }


    public String saveImageToS3(String path, String fileName, Optional<Map<String, String>> optionalMetadata, MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();

        optionalMetadata.ifPresent(map -> {
            if (!map.isEmpty()) {
                map.forEach(metadata::addUserMetadata);
            }
        });

        try {
            amazonS3.putObject(path, fileName, file.getInputStream(), metadata);
            return amazonS3.getUrl(path, fileName).toString();
        } catch (AmazonServiceException e) {
            throw new AmazonServiceException("Failed to store file to s3", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, String> extractMetadata(MultipartFile file) {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));
        return metadata;
    }

    private void checkImageFile(MultipartFile file) {
        if (!Arrays.asList(
                IMAGE_JPEG.getMimeType(),
                IMAGE_PNG.getMimeType()).contains(file.getContentType())) {
            throw new AppException("File phải có định dạng hình ảnh");
        }
    }



}
