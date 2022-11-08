package com.example.datnbackend.service.impl;

import com.example.datnbackend.dto.exception.AppException;
import com.example.datnbackend.dto.post.PostCreateRequest;
import com.example.datnbackend.dto.post.PostDescriptionResponse;
import com.example.datnbackend.dto.post.PostDetailResponse;
import com.example.datnbackend.dto.user.UserDescriptionPostDetailResponse;
import com.example.datnbackend.entity.*;
import com.example.datnbackend.repository.*;
import com.example.datnbackend.security.UserPrincipal;
import com.example.datnbackend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TypeEstateRepository typeEstateRepository;
    @Autowired
    WardsRepository wardsRepository;
    @Autowired
    ReviewRepository reviewRepository;

    @Override
    public PostDetailResponse createPost(PostCreateRequest requestBody) {
        if(requestBody.getTitle() == null || requestBody.getTitle().isEmpty()){
            throw new AppException("Title không được null hoặc trống");
        }
        if(requestBody.getDescription() == null || requestBody.getDescription().isEmpty()){
            throw new AppException("Phần mô tả không được null hoặc trống");
        }
        if(requestBody.getWardsId() == null){
            throw new AppException("Địa chỉ không được null");
        }
        if(requestBody.getPriceMonth() == null){
            throw new AppException("Giá không được null");
        }
        if(requestBody.getArea() == null){
            throw new AppException("Diện tích không được null");
        }

        PostEntity postEntity = new PostEntity();
        postEntity.setTitle(requestBody.getTitle());
        postEntity.setDescription(requestBody.getDescription());

        if(requestBody.getTypeEstateId() != null){
            TypeEstateEntity typeEstateEntity = typeEstateRepository.findOneById(requestBody.getTypeEstateId());
            if(typeEstateEntity == null){
                throw new AppException("Không tìm thấy kiểu bất động sản");
            }
            postEntity.setTypeEstate(typeEstateEntity);
        }

        WardsEntity wardsEntity = wardsRepository.findOneById(requestBody.getWardsId());
        if(wardsEntity == null){
            throw new AppException("Không tìm thấy địa chỉ");
        }
        postEntity.setWards(wardsEntity);

        postEntity.setAddressDetail(requestBody.getAddressDetail());
        postEntity.setArea(requestBody.getArea());
        postEntity.setPriceMonth(requestBody.getPriceMonth());
        postEntity.setFurniture(requestBody.getFurniture());
        postEntity.setRoom(requestBody.getRoom());
        postEntity.setBathRoom(requestBody.getBathRoom());
        postEntity.setDeleted(false);
        postEntity.setHide(requestBody.getHide() == null ? false : requestBody.getHide());
        postEntity.setLocked(false);
        postEntity.setVerified(false);
        postEntity.setView(0);
        postEntity.setCreatedBy(getCurrentUser());

        if(requestBody.getExpiredDate() != null){
            LocalDateTime currentTime = getCurrentDateUTC();
            if(currentTime.plusHours(1).isAfter(requestBody.getExpiredDate())){
                throw new AppException("Expired date phải sau thời gian hiện tại");
            }
        }
        postEntity.setExpiredDate(requestBody.getExpiredDate());

        postEntity = postRepository.save(postEntity);

        return new PostDetailResponse(postEntity.getId(), postEntity.getTitle(), postEntity.getDescription(),
                postEntity.getTypeEstate().getName(), postEntity.getWards().getDistrict().getProvince().getName(),
                postEntity.getWards().getDistrict().getName(), postEntity.getWards().getName(), postEntity.getAddressDetail(),
                postEntity.getArea(), postEntity.getPriceMonth(), postEntity.getFurniture(), postEntity.getRoom(),
                postEntity.getBathRoom(), postEntity.getExpiredDate(), postEntity.getDeleted(), postEntity.getHide(),
                postEntity.getLocked(), postEntity.getVerified(), postEntity.getView(),
                convertEntityToDescriptionPostDetailResponse(postEntity.getCreatedBy()), postEntity.getCreatedDate(),
                postEntity.getModifiedDate());
    }

    @Override
    public PostDetailResponse updatePost(Long id, PostCreateRequest requestBody) {
        PostEntity postEntity = postRepository.findOneByIdAndDeletedFalseAndLockedFalse(id);

        if(postEntity == null){
            throw new AppException("Không tìm thấy bài đăng với id: " + id);
        }

        if(getCurrentUser().getId() != postEntity.getCreatedBy().getId()){
            throw new AppException("Không có quyền chỉnh sửa bài đăng này");
        }

        if(requestBody.getTitle() != null && !requestBody.getTitle().isEmpty()){
            postEntity.setTitle(requestBody.getTitle());
        }
        if(requestBody.getDescription() != null && !requestBody.getDescription().isEmpty()){
            postEntity.setDescription(requestBody.getDescription());
        }
        if(requestBody.getTypeEstateId() != null){
            TypeEstateEntity typeEstateEntity = typeEstateRepository.findOneById(requestBody.getTypeEstateId());
            if(typeEstateEntity == null){
                throw new AppException("Không tìm thấy kiểu bất động sản");
            }
            postEntity.setTypeEstate(typeEstateEntity);
        }
        if(requestBody.getWardsId() != null){
            WardsEntity wardsEntity = wardsRepository.findOneById(requestBody.getWardsId());
            if(wardsEntity == null){
                throw new AppException("Không tìm thấy địa chỉ");
            }
            postEntity.setWards(wardsEntity);
        }
        if(requestBody.getAddressDetail() != null){
            postEntity.setAddressDetail(requestBody.getAddressDetail());
        }
        if(requestBody.getArea() != null){
            postEntity.setArea(requestBody.getArea());
        }
        if(requestBody.getPriceMonth() != null){
            postEntity.setPriceMonth(requestBody.getPriceMonth());
        }
        if(requestBody.getFurniture() != null){
            postEntity.setFurniture(requestBody.getFurniture());
        }
        if(requestBody.getRoom() != null){
            postEntity.setRoom(requestBody.getRoom());
        }
        if(requestBody.getBathRoom() != null){
            postEntity.setBathRoom(requestBody.getBathRoom());
        }
        if(requestBody.getExpiredDate() != null){
            if(getCurrentDateUTC().plusHours(1).isAfter(requestBody.getExpiredDate())){
                throw new AppException("Expired date phải sau thời gian hiện tại ít nhất 1 tiếng");
            }
            postEntity.setExpiredDate(requestBody.getExpiredDate());
        }
        postEntity.setModifiedDate(getCurrentDateUTC());

        postEntity = postRepository.save(postEntity);
        return new PostDetailResponse(postEntity.getId(), postEntity.getTitle(), postEntity.getDescription(),
                postEntity.getTypeEstate().getName(), postEntity.getWards().getDistrict().getProvince().getName(),
                postEntity.getWards().getDistrict().getName(), postEntity.getWards().getName(), postEntity.getAddressDetail(),
                postEntity.getArea(), postEntity.getPriceMonth(), postEntity.getFurniture(), postEntity.getRoom(),
                postEntity.getBathRoom(), postEntity.getExpiredDate(), postEntity.getDeleted(), postEntity.getHide(),
                postEntity.getLocked(), postEntity.getVerified(), postEntity.getView(),
                convertEntityToDescriptionPostDetailResponse(postEntity.getCreatedBy()), postEntity.getCreatedDate(),
                postEntity.getModifiedDate());
    }

    @Override
    public List<PostDescriptionResponse> getPostDescriptionList(Integer page, Integer size, String order, Long province, Long district, Long wards, String address, List<Long> type, Integer room, Double pricemin, Double pricemax) {
        List<String> orderType = Arrays.asList("DATEASC", "DATEDESC", "PRICEASC", "PRICEDESC", "AREAASC", "AREADESC");
        if(order == null || !orderType.contains(order.toUpperCase())){
            order = null;
        }else {
            order = order.toUpperCase();
        }
        Pageable pageable = PageRequest.of(page, size);
        List<PostEntity> postEntityList = postRepository.findAllWithFilterWithDeletedFalseAndHideFalseAndLockedFalse(order, province, district, wards,
                address, type, room, pricemin, pricemax, pageable);

        if(postEntityList.isEmpty()){
            return Collections.emptyList();
        }
        List<PostDescriptionResponse> postDescriptionResponseList = postEntityList.stream()
                .map(o -> convertPostEntityToPostDescription(o)).collect(Collectors.toList());
        return postDescriptionResponseList;
    }


    @Override
    public PostDetailResponse getPostDetail(Long id) {
        PostEntity postEntity = postRepository.findOneByIdAndDeletedFalseAndHideFalseAndLockedFalse(id);
        if(postEntity == null){
            throw new AppException("Không tìm thấy bài đăng với id: " + id);
        }

        PostDetailResponse postDetailResponse = new PostDetailResponse(postEntity.getId(), postEntity.getTitle(), postEntity.getDescription(),
                postEntity.getTypeEstate().getName(), postEntity.getWards().getDistrict().getProvince().getName(),
                postEntity.getWards().getDistrict().getName(), postEntity.getWards().getName(), postEntity.getAddressDetail(),
                postEntity.getArea(), postEntity.getPriceMonth(), postEntity.getFurniture(), postEntity.getRoom(),
                postEntity.getBathRoom(), postEntity.getExpiredDate(), postEntity.getDeleted(), postEntity.getHide(),
                postEntity.getLocked(), postEntity.getVerified(), postEntity.getView(),
                convertEntityToDescriptionPostDetailResponse(postEntity.getCreatedBy()), postEntity.getCreatedDate(),
                postEntity.getModifiedDate());

        Integer view = postEntity.getView() + 1;
        postEntity.setView(view);
        postRepository.save(postEntity);

        return postDetailResponse;
    }


    @Override
    public void hidePost(Long id, Boolean hide) {
        PostEntity postEntity = postRepository.findOneByIdAndDeletedFalseAndLockedFalse(id);
        if(postEntity == null){
            throw new AppException("Không tìm thấy bài đăng với id: " + id);
        }

        if(getCurrentUser().getId() != postEntity.getCreatedBy().getId()){
            throw new AppException("Không có quyền chỉnh sửa bài đăng này");
        }

        postEntity.setHide(hide);
        postRepository.save(postEntity);
    }

    @Override
    @Transactional
    public void deleteMultiplePost(List<Long> ids) {
        Long id = getCurrentUser().getId();
        for(Long i : ids){
            PostEntity postEntity = postRepository.findOneByIdAndDeletedFalse(i);
            if(postEntity == null){
                throw new AppException("Có id không tìm thấy");
            }
            if(id != postEntity.getCreatedBy().getId()){
                throw new AppException("Có bài viết không có quyền truy cập");
            }
            postEntity.setDeleted(true);
            postRepository.save(postEntity);
        }
    }

    @Override
    public void changeStateOfHideLockedVerified(Long id, Boolean hide, Boolean locked, Boolean verified) {
        PostEntity postEntity = postRepository.findOneByIdAndDeletedFalse(id);
        if(postEntity == null){
            throw new AppException("Không tìm thấy bài đăng với id: " + id);
        }
        if(hide != null){
            postEntity.setHide(hide);
        }
        if(locked != null){
            postEntity.setLocked(locked);
        }
        if(verified != null){
            postEntity.setVerified(verified);
        }

        postRepository.save(postEntity);
    }


    @Override
    public void savePostToUser(Long id) {
        UserEntity currentUser = getCurrentUser();
        PostEntity postEntity = postRepository.findOneByIdAndDeletedFalseAndHideFalseAndLockedFalse(id);
        if(postEntity == null){
            throw new AppException("Not found post with id: " + id);
        }

        List<PostEntity> postEntityList = currentUser.getPostSave();
        postEntityList.add(postEntity);
        currentUser.setPostSave(postEntityList);
        userRepository.save(currentUser);
    }

    private UserEntity getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity userEntity = userRepository.findOneByIdAndDeletedFalseAndLockedFalse(userPrincipal.getId());
        if(userEntity == null){
            throw new AppException("Not found current user");
        }
        return userEntity;
    }

    private LocalDateTime getCurrentDateUTC(){
        return LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC);
    }

    private LocalDateTime convertDateUtcToDateLocal(LocalDateTime dateTime){
        return dateTime.atZone(ZoneOffset.UTC).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    private LocalDateTime convertDateLocalToDateUtc(LocalDateTime dateTime){
        return dateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    private UserDescriptionPostDetailResponse convertEntityToDescriptionPostDetailResponse(UserEntity userEntity){
        if(userEntity == null){
            return null;
        }
        UserDescriptionPostDetailResponse userDescriptionPostDetailResponse = new UserDescriptionPostDetailResponse();
        List<ReviewEntity> reviewEntityList = reviewRepository.findAllByUserBusinessIdAndDeletedFalse(userEntity.getId());
        Integer totalPoint = 0;
        if(reviewEntityList.isEmpty()){
            userDescriptionPostDetailResponse.setRatingPoint(null);
        }else {
            for(ReviewEntity r : reviewEntityList){
                totalPoint = totalPoint + r.getRatingPoint();
            }
            userDescriptionPostDetailResponse.setRatingPoint((Double.valueOf(totalPoint))/reviewEntityList.size());
        }
        userDescriptionPostDetailResponse.setId(userEntity.getId());
        userDescriptionPostDetailResponse.setUsername(userEntity.getUsername());
        userDescriptionPostDetailResponse.setFirstName(userEntity.getFirstName());
        userDescriptionPostDetailResponse.setLastName(userEntity.getLastName());
        userDescriptionPostDetailResponse.setEmail(userEntity.getEmail());
        userDescriptionPostDetailResponse.setPhone(userEntity.getPhone());
        userDescriptionPostDetailResponse.setImageUrl(userEntity.getImageUrl());
        return userDescriptionPostDetailResponse;
    }

    private PostDescriptionResponse convertPostEntityToPostDescription(PostEntity postEntity){
        List<PostImageEntity> postImageEntityList = postEntity.getPostImageList();
        PostImageEntity mainImageEntity;
        if(!postImageEntityList.isEmpty()){
            mainImageEntity = postImageEntityList.stream().filter(o -> o.getMainImage() == true).findFirst().get();
        }else {
            mainImageEntity = null;
        }
        return new PostDescriptionResponse(postEntity.getId(), postEntity.getTitle(), postEntity.getTypeEstate().getName(),
                postEntity.getWards().getDistrict().getProvince().getName(), postEntity.getWards().getDistrict().getName(),
                postEntity.getWards().getName(), postEntity.getArea(), postEntity.getPriceMonth(), postEntity.getVerified(),
                postEntity.getCreatedBy().getFirstName() + " " + postEntity.getCreatedBy().getLastName(),
                postImageEntityList.isEmpty() ? 1 : postImageEntityList.size(),  mainImageEntity == null ? null : mainImageEntity.getUrl(),
                postEntity.getCreatedDate());
    }
}
