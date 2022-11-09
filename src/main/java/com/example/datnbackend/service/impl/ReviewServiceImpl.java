package com.example.datnbackend.service.impl;

import com.example.datnbackend.dto.exception.AppException;
import com.example.datnbackend.dto.review.ReviewCreateRequest;
import com.example.datnbackend.dto.review.ReviewResponse;
import com.example.datnbackend.dto.user.UserDescriptionReviewResponse;
import com.example.datnbackend.entity.ReviewEntity;
import com.example.datnbackend.entity.UserEntity;
import com.example.datnbackend.repository.ReviewRepository;
import com.example.datnbackend.repository.UserRepository;
import com.example.datnbackend.security.UserPrincipal;
import com.example.datnbackend.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewServiceImpl implements ReviewService {
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    public ReviewResponse createReview(Long id, ReviewCreateRequest requestBody) {
        if(requestBody.getRatingPoint() == null || requestBody.getRatingPoint() < 1 || requestBody.getRatingPoint() > 5){
            throw new AppException("Không được null và phải trong khoảng từ 1 đến 5");
        }

        UserEntity businessUser = userRepository.findOneByIdAndDeletedFalseWithRoleBusiness(id);
        if(businessUser == null){
            throw new AppException("Không tìm thấy tài khoản business");
        }

        ReviewEntity reviewEntity = new ReviewEntity();
        reviewEntity.setUserBusiness(businessUser);
        reviewEntity.setRatingPoint(requestBody.getRatingPoint());
        reviewEntity.setDescription(reviewEntity.getDescription());
        reviewEntity.setDeleted(false);
        reviewEntity.setCreatedBy(getCurrentUserEntity());

        reviewEntity = reviewRepository.save(reviewEntity);

        return convertEntityToResponse(reviewEntity);
    }

    @Override
    public ReviewResponse updateReview(Long id, ReviewCreateRequest requestBody) {
        ReviewEntity reviewEntity = reviewRepository.findOneByIdAndDeletedFalse(id);
        if(reviewEntity == null){
            throw new AppException("Không tìm thấy đánh giá");
        }
        if(reviewEntity.getCreatedBy().getId() != getCurrentUserEntity().getId()){
            throw new AppException("Không có quyền chỉnh sửa đánh giá");
        }

        if(requestBody.getRatingPoint() != null){
            if(requestBody.getRatingPoint() < 1 || requestBody.getRatingPoint() > 5){
                throw new AppException("Phải trong khoảng từ 1 đến 5");
            }
            reviewEntity.setRatingPoint(requestBody.getRatingPoint());
        }

        if(requestBody.getDescription() != null){
            reviewEntity.setDescription(requestBody.getDescription());
        }

        reviewEntity = reviewRepository.save(reviewEntity);
        return convertEntityToResponse(reviewEntity);
    }


    @Override
    public List<ReviewResponse> getReviewList(Integer page, Integer size, String order, Long id) {
        if(order != null && order.equalsIgnoreCase("DATEDESC")){
            order = order.toUpperCase();
        }else{
            order = null;
        }

        UserEntity userEntity = userRepository.findOneByIdAndDeletedFalseWithRoleBusiness(id);
        if(userEntity == null){
            throw new AppException("Không tìm thấy tài khoản Business");
        }
        if(!userEntity.getDisplayReview()){
            throw new AppException("Không hiển thị đánh giá đối với tài khoản này");
        }

        Pageable pageable = PageRequest.of(page, size);
        List<ReviewEntity> reviewEntityList = reviewRepository
                .findAllByUserBusinessIdAndDeletedFalseWithPaging(id, order, pageable);

        if(reviewEntityList.isEmpty()){
            return Collections.emptyList();
        }
        return reviewEntityList.stream().map(o -> convertEntityToResponse(o)).collect(Collectors.toList());
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

    private ReviewResponse convertEntityToResponse(ReviewEntity reviewEntity){
        UserEntity createdBy = reviewEntity.getCreatedBy();
        return new ReviewResponse(reviewEntity.getId(), reviewEntity.getDescription(), reviewEntity.getRatingPoint(),
                new UserDescriptionReviewResponse(createdBy.getId(), createdBy.getUsername(), createdBy.getFirstName(),
                        createdBy.getLastName(), createdBy.getImageUrl()), reviewEntity.getCreatedDate());
    }
}
