package com.example.datnbackend.service.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.datnbackend.dto.exception.AppException;
import com.example.datnbackend.dto.post.PostImageResponse;
import com.example.datnbackend.entity.PostEntity;
import com.example.datnbackend.entity.PostImageEntity;
import com.example.datnbackend.entity.UserEntity;
import com.example.datnbackend.repository.PostImageRepository;
import com.example.datnbackend.repository.PostRepository;
import com.example.datnbackend.repository.UserRepository;
import com.example.datnbackend.security.UserPrincipal;
import com.example.datnbackend.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.http.entity.ContentType.*;

@Service
public class ImageServiceImpl implements ImageService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    PostImageRepository postImageRepository;
//    @Autowired
//    AmazonS3 amazonS3;
    @Value("${aws.bucketName}")
    private String bucketName;
    @Value("${image.post.default}")
    private String imageDefaultUrl;
    @Value("${image.avatar.default}")
    private String avatarDefaultUrl;
    @Override
    public void updateAvatarImage(MultipartFile file) {
        if(file == null || file.isEmpty()){
            throw new IllegalStateException("File trống");
        }

        UserEntity userEntity = getCurrentUserEntity();

        checkImageFile(file);
        Map<String, String> metadata = extractMetadata(file);

        String filename = String.format("%s/%s", "avatar", UUID.randomUUID());
        String imageUrl;

        try {
//            imageUrl = saveImageToS3(bucketName, filename, Optional.of(metadata), file);
            userEntity.setImageUrl(avatarDefaultUrl);
            userRepository.save(userEntity);
        }catch (Exception e){
            throw new AppException(e.getMessage());
        }

    }

    @Override
    public void uploadPostImage(Long id, List<MultipartFile> files, Integer main) {
        if(files == null || files.isEmpty()){
            throw new AppException("Không có files");
        }

        int size = files.size();
        if(size > 10){
            throw new AppException("Tối đa 10 ảnh");
        }

        if(id == null){
            throw new AppException("Không có id bài viết");
        }

        PostEntity postEntity = postRepository.findOneByIdAndDeletedFalseAndLockedFalse(id);
        if(postEntity == null){
            throw new AppException("Không tìm thấy bài viết");
        }

        UserEntity currentUser = getCurrentUserEntity();

        if(currentUser.getId().equals(postEntity.getCreatedBy().getId())){
            throw new AppException("Không có quyền sửa bài viết");
        }

        List<PostImageEntity> postImageEntityList = postImageRepository.findAllByPostIdAndDeletedFalse(id);
        if(postImageEntityList.isEmpty()){
            if(main == null || main < 1 || main > size){
                throw new AppException("Phải chọn main image");
            }
        }else {
            if(main != null && main >= 1 && main <= size) {
                PostImageEntity postImageEntity = postImageRepository.findOneByPostIdAndDeletedFalseAndMainImageTrue(id);
                if (postImageEntity == null) {
                    throw new AppException("Không thành công");
                }

                postImageEntity.setMainImage(false);
                postImageRepository.save(postImageEntity);
            }else {
                main = null;
            }
        }

        for (int i = 0 ; i < size; i++){
            MultipartFile file = files.get(i);
            if(file == null || file.isEmpty()){
                continue;
            }

            PostImageEntity postImageEntity = new PostImageEntity();
            postImageEntity.setPost(postEntity);
            postImageEntity.setDeleted(false);
            if(main != null && i + 1 == main){
                postImageEntity.setMainImage(true);
            }else {
                postImageEntity.setMainImage(false);
            }

            checkImageFile(file);
            Map<String, String> metadata = extractMetadata(file);

            String filename = String.format("%s/%s", "post", UUID.randomUUID());
            String imageUrl;

            try {
                imageUrl = saveImageToS3(bucketName, filename, Optional.of(metadata), file);
            }catch (Exception e){
                throw new AppException(e.getMessage());
            }

            postImageEntity.setUrl(imageUrl);
            postImageRepository.save(postImageEntity);
        }
    }

    @Override
    public List<PostImageResponse> getPostImage(Long id) {
        PostEntity postEntity = postRepository.findOneByIdAndDeletedFalseAndLockedFalse(id);
        if(postEntity == null){
            throw new AppException("Không tìm thấy bài viết");
        }

        UserEntity currentUser = getCurrentUserEntity();

        if(!currentUser.getId().equals(postEntity.getCreatedBy().getId())){
            throw new AppException("Không có quyền");
        }

        List<PostImageEntity> postImageEntityList = postImageRepository.findAllByPostIdAndDeletedFalse(id);
        if(postImageEntityList.isEmpty()){
            return Collections.emptyList();
        }

        return postImageEntityList.stream().map(o -> new PostImageResponse(o.getId(), o.getUrl(), o.getMainImage())).collect(Collectors.toList());
    }

    @Override
    public void changeMainImage(Long id) {
        PostImageEntity postImageEntity = postImageRepository.findOneByIdAndDeletedFalse(id);
        if(postImageEntity == null){
            throw new AppException("Không tìm thấy ảnh");
        }

        PostEntity postEntity = postImageEntity.getPost();
        if(!postEntity.getCreatedBy().getId().equals(getCurrentUserEntity().getId())){
            throw new AppException("Không có quyền chỉnh sửa");
        }

        PostImageEntity postMainImageEntity = postImageRepository.findOneByPostIdAndDeletedFalseAndMainImageTrue(postEntity.getId());
        if(postMainImageEntity == null){
            throw new AppException("Không tìm thấy ảnh");
        }

        postMainImageEntity.setMainImage(false);
        postImageEntity.setMainImage(true);

        postImageRepository.save(postMainImageEntity);
        postImageRepository.save(postImageEntity);
    }

    @Override
    @Transactional
    public void deleteImage(List<Long> ids) {
        if(ids == null || ids.isEmpty()){
            throw new AppException("Xoá không thành công");
        }
        UserEntity currentUser = getCurrentUserEntity();

        List<PostImageEntity> postImageEntityList = postImageRepository.findAllByIdInAndCreatedByAndMainImageFalse(ids, currentUser.getId());

        if(postImageEntityList.isEmpty() || postImageEntityList.size() != ids.size()){
            throw new AppException("Không được xoá main image hoặc có id không tồn tại");
        }

        for(PostImageEntity i : postImageEntityList){
            i.setDeleted(true);
            postImageRepository.save(i);
        }

    }


    public String saveImageToS3(String path, String fileName, Optional<Map<String, String>> optionalMetadata, MultipartFile file) {
//        ObjectMetadata metadata = new ObjectMetadata();
//
//        optionalMetadata.ifPresent(map -> {
//            if (!map.isEmpty()) {
//                map.forEach(metadata::addUserMetadata);
//            }
//        });
//
//        try {
//            amazonS3.putObject(path, fileName, file.getInputStream(), metadata);
//            return amazonS3.getUrl(path, fileName).toString();
//        } catch (AmazonServiceException e) {
//            throw new AmazonServiceException("Failed to store file to s3", e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        return imageDefaultUrl;
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

    private UserEntity getCurrentUserEntity(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity userEntity = userRepository.findOneByIdAndDeletedFalseAndLockedFalse(userPrincipal.getId());
        if(userEntity == null){
            throw new AppException("Not found current user");
        }
        return userEntity;
    }


}
