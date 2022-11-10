package com.example.datnbackend.service.impl;

import com.example.datnbackend.dto.exception.AppException;
import com.example.datnbackend.dto.post.*;
import com.example.datnbackend.dto.user.UserDescriptionPostDetailResponse;
import com.example.datnbackend.dto.user.UserDescriptionReviewResponse;
import com.example.datnbackend.entity.*;
import com.example.datnbackend.repository.*;
import com.example.datnbackend.security.UserPrincipal;
import com.example.datnbackend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    TypeReportRepository typeReportRepository;
    @Autowired
    WardsRepository wardsRepository;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    PostImageRepository postImageRepository;
    @Autowired
    PostReportRepository postReportRepository;
    @Autowired
    PostSaveRepository postSaveRepository;

    @Override
    public PostDetailForBusinessResponse createPost(PostCreateRequest requestBody) {
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
        postEntity.setCreatedBy(getCurrentUserEntity());

        if(requestBody.getExpiredDate() != null){
            LocalDateTime currentTime = getCurrentDateUTC();
            if(currentTime.plusHours(1).isAfter(requestBody.getExpiredDate())){
                throw new AppException("Expired date phải sau thời gian hiện tại");
            }
        }
        postEntity.setExpiredDate(requestBody.getExpiredDate());

        postEntity = postRepository.save(postEntity);


        return new PostDetailForBusinessResponse(postEntity.getId(), postEntity.getTitle(), postEntity.getDescription(),
                postEntity.getTypeEstate().getName(), postEntity.getWards().getDistrict().getProvince().getName(),
                postEntity.getWards().getDistrict().getName(), postEntity.getWards().getName(), postEntity.getAddressDetail(),
                postEntity.getArea(), postEntity.getPriceMonth(), postEntity.getFurniture(), postEntity.getRoom(),
                postEntity.getBathRoom(), postEntity.getExpiredDate(), postEntity.getDeleted(), postEntity.getHide(),
                postEntity.getLocked(), postEntity.getVerified(), postEntity.getView(), getPostImageListByPostId(postEntity.getId()),
                convertEntityToDescriptionPostDetailResponse(postEntity.getCreatedBy()), postEntity.getCreatedDate(),
                postEntity.getModifiedDate());
    }

    @Override
    public PostDetailForBusinessResponse updatePost(Long id, PostCreateRequest requestBody) {
        PostEntity postEntity = postRepository.findOneByIdAndDeletedFalseAndLockedFalse(id);

        if(postEntity == null){
            throw new AppException("Không tìm thấy bài đăng với id: " + id);
        }

        if(getCurrentUserEntity().getId() != postEntity.getCreatedBy().getId()){
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
        return new PostDetailForBusinessResponse(postEntity.getId(), postEntity.getTitle(), postEntity.getDescription(),
                postEntity.getTypeEstate().getName(), postEntity.getWards().getDistrict().getProvince().getName(),
                postEntity.getWards().getDistrict().getName(), postEntity.getWards().getName(), postEntity.getAddressDetail(),
                postEntity.getArea(), postEntity.getPriceMonth(), postEntity.getFurniture(), postEntity.getRoom(),
                postEntity.getBathRoom(), postEntity.getExpiredDate(), postEntity.getDeleted(), postEntity.getHide(),
                postEntity.getLocked(), postEntity.getVerified(), postEntity.getView(), getPostImageListByPostId(postEntity.getId()),
                convertEntityToDescriptionPostDetailResponse(postEntity.getCreatedBy()), postEntity.getCreatedDate(),
                postEntity.getModifiedDate());
    }

    @Override
    public List<PostDescriptionResponse> getPostDescriptionList(Integer page, Integer size, String order,
                                                                Long province, Long district, Long wards,
                                                                List<Long> type, Integer room, Double pricemin,
                                                                Double pricemax, Double areamin, Double areamax) {
        List<String> orderType = Arrays.asList("DATEASC", "DATEDESC", "PRICEASC", "PRICEDESC", "AREAASC", "AREADESC");
        if(order == null || !orderType.contains(order.toUpperCase())){
            order = null;
        }else {
            order = order.toUpperCase();
        }
        Pageable pageable = PageRequest.of(page, size);
        List<PostEntity> postEntityList = postRepository.findAllWithFilterWithDeletedFalseAndHideFalseAndLockedFalse(order, province, district, wards,
                 type, room, pricemin, pricemax, areamin, areamax, pageable);

        if(postEntityList.isEmpty()){
            return Collections.emptyList();
        }
        return postEntityList.stream()
                .map(o -> convertPostEntityToPostDescription(o)).collect(Collectors.toList());
    }

    @Override
    public List<PostDescriptionResponse> getPostDescriptionListSearch(Integer page, Integer size, String order, String search) {
        List<String> orderType = Arrays.asList("DATEASC", "DATEDESC", "PRICEASC", "PRICEDESC", "AREAASC", "AREADESC");
        if(order == null || !orderType.contains(order.toUpperCase())){
            order = null;
        }else {
            order = order.toUpperCase();
        }
        Pageable pageable = PageRequest.of(page, size);
        if(search == null || search.isEmpty()){
            search = null;
        }else {
            search = search.toUpperCase();
        }

        List<PostEntity> postEntityList = postRepository
                .findAllWithSearchWithDeletedFalseAndHideFalseAndLockedFalse(order, search, pageable);

        if(postEntityList.isEmpty()){
            postEntityList = postRepository.findAllWithFilterWithDeletedFalseAndHideFalseAndLockedFalse(order, null,
                    null, null, null, null, null, null, null, null, pageable);
        }

        if(postEntityList.isEmpty()){
            return Collections.emptyList();
        }
        return postEntityList.stream()
                .map(o -> convertPostEntityToPostDescription(o)).collect(Collectors.toList());
    }


    @Override
    public List<PostDescriptionForAdminBusinessResponse> getPostDescriptionListForBusiness(Integer page, Integer size) {
        UserEntity currentUser = getCurrentUserEntity();
        Pageable pageable = PageRequest.of(page, size);
        List<PostEntity> postEntityList = postRepository.findAllByCreatedByIdAndDeletedFalse(currentUser.getId(), pageable);
        if(postEntityList.isEmpty()){
            return Collections.emptyList();
        }
        return postEntityList.stream()
                .map(o -> convertPostEntityToPostDescriptionForAdminBusiness(o)).collect(Collectors.toList());
    }

    @Override
    public List<PostDescriptionForAdminBusinessResponse> getPostDescriptionListForAdmin(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        List<PostEntity> postEntityList = postRepository.findAllByDeletedFalse(pageable);
        if(postEntityList.isEmpty()){
            return Collections.emptyList();
        }
        return postEntityList.stream()
                .map(o -> convertPostEntityToPostDescriptionForAdminBusiness(o)).collect(Collectors.toList());
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
                postEntity.getBathRoom(), postEntity.getExpiredDate(), postEntity.getVerified(), postEntity.getView(),
                getPostImageListByPostId(postEntity.getId()), convertEntityToDescriptionPostDetailResponse(postEntity.getCreatedBy()),
                postEntity.getCreatedDate(), postEntity.getModifiedDate());

        Integer view = postEntity.getView() + 1;
        postEntity.setView(view);
        postRepository.save(postEntity);

        return postDetailResponse;
    }

    @Override
    public PostDetailForAdminResponse getPostDetailForAdmin(Long id) {
        PostEntity postEntity = postRepository.findOneByIdAndDeletedFalse(id);
        if(postEntity == null){
            throw new AppException("Không tìm thấy bài đăng với id: " + id);
        }

        return new PostDetailForAdminResponse(postEntity.getId(), postEntity.getTitle(), postEntity.getDescription(),
                postEntity.getTypeEstate().getName(), postEntity.getWards().getDistrict().getProvince().getName(),
                postEntity.getWards().getDistrict().getName(), postEntity.getWards().getName(), postEntity.getAddressDetail(),
                postEntity.getArea(), postEntity.getPriceMonth(), postEntity.getFurniture(), postEntity.getRoom(),
                postEntity.getBathRoom(), postEntity.getExpiredDate(), postEntity.getDeleted(), postEntity.getHide(),
                postEntity.getLocked(), postEntity.getVerified(), postEntity.getView(), getPostImageListByPostId(postEntity.getId()),
                convertEntityToDescriptionPostDetailResponse(postEntity.getCreatedBy()), postEntity.getCreatedDate(),
                postEntity.getModifiedDate());
    }

    @Override
    public PostDetailForBusinessResponse getPostDetailForBusiness(Long id) {
        UserEntity userEntity = getCurrentUserEntity();

        PostEntity postEntity = postRepository.findOneByIdAndDeletedFalse(id);
        if(postEntity == null){
            throw new AppException("Không tìm thấy bài đăng với id: " + id);
        }

        if(userEntity.getId() != postEntity.getCreatedBy().getId()){
            throw new AppException("Không có quyền truy cập bài đăng này");
        }
        return new PostDetailForBusinessResponse(postEntity.getId(), postEntity.getTitle(), postEntity.getDescription(),
                postEntity.getTypeEstate().getName(), postEntity.getWards().getDistrict().getProvince().getName(),
                postEntity.getWards().getDistrict().getName(), postEntity.getWards().getName(), postEntity.getAddressDetail(),
                postEntity.getArea(), postEntity.getPriceMonth(), postEntity.getFurniture(), postEntity.getRoom(),
                postEntity.getBathRoom(), postEntity.getExpiredDate(), postEntity.getDeleted(), postEntity.getHide(),
                postEntity.getLocked(), postEntity.getVerified(), postEntity.getView(), getPostImageListByPostId(postEntity.getId()),
                convertEntityToDescriptionPostDetailResponse(postEntity.getCreatedBy()), postEntity.getCreatedDate(),
                postEntity.getModifiedDate());
    }


    @Override
    public void hidePost(Long id, Boolean hide) {
        PostEntity postEntity = postRepository.findOneByIdAndDeletedFalseAndLockedFalse(id);
        if(postEntity == null){
            throw new AppException("Không tìm thấy bài đăng với id: " + id);
        }

        if(getCurrentUserEntity().getId() != postEntity.getCreatedBy().getId()){
            throw new AppException("Không có quyền chỉnh sửa bài đăng này");
        }

        postEntity.setHide(hide);
        postRepository.save(postEntity);
    }

    @Override
    @Transactional
    public void deleteMultiplePost(List<Long> ids) {
        Long id = getCurrentUserEntity().getId();
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
        UserEntity currentUser = getCurrentUserEntity();
        PostEntity postEntity = postRepository.findOneByIdAndDeletedFalseAndHideFalseAndLockedFalse(id);
        if(postEntity == null){
            throw new AppException("Not found post with id: " + id);
        }

        PostSaveEntity postSaveEntity = new PostSaveEntity();
        postSaveEntity.setUser(currentUser);
        postSaveEntity.setPost(postEntity);

        postSaveRepository.save(postSaveEntity);
    }

    @Override
    public List<PostDescriptionResponse> getDescriptionPostListSave(Integer page, Integer size) {
        UserEntity userEntity = getCurrentUserEntity();
        Pageable pageable = PageRequest.of(page, size);
        List<PostEntity> postEntityList = postRepository.findAllByUserIdWithDeletedFalseAndHideFalseAndLockedFalse(userEntity.getId(), pageable);

        if(postEntityList.isEmpty()){
            return Collections.emptyList();
        }
        return postEntityList.stream()
                .map(o -> convertPostEntityToPostDescription(o)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletePostSave(List<Long> ids) {
        if(ids == null || ids.isEmpty()){
            throw new AppException("Xoá không thành công");
        }
        UserEntity userEntity = getCurrentUserEntity();
        List<PostSaveEntity> postSaveEntityList = postSaveRepository.findAllByUserIdAndPostIdIn(userEntity.getId(), ids);

        if(postSaveEntityList.size() != ids.size()){
            throw new AppException("Có id không tìm thấy");
        }

        postSaveRepository.deleteByUserIdAndPostIdIn(userEntity.getId(), ids);
    }


    @Override
    public PostReportDetailResponse createPostReport(Long id, PostReportCreateRequest requestBody) {
        if(requestBody.getTypeReportId() == null){
            throw new AppException("Phải chọn loại báo cáo bài đăng");
        }
        if(requestBody.getEmailReport() == null && requestBody.getPhoneReport() == null){
            throw new AppException("Cần điền thông tin email hoặc số điện thoại");
        }

        PostEntity postEntity = postRepository.findOneByIdAndDeletedFalseAndHideFalseAndLockedFalse(id);
        if(postEntity == null){
            throw new AppException("Không tìm thấy bài đăng với id: " + id);
        }

        TypeReportEntity typeReportEntity = typeReportRepository.findOneById(requestBody.getTypeReportId());
        if(typeReportEntity == null){
            throw new AppException("Không tìm thấy báo cáo bài đăng với id: " + requestBody.getTypeReportId());
        }

        PostReportEntity postReportEntity = new PostReportEntity();
        postReportEntity.setPost(postEntity);
        postReportEntity.setTypeReport(typeReportEntity);
        postReportEntity.setEmailReport(requestBody.getEmailReport());
        postReportEntity.setPhoneReport(requestBody.getPhoneReport());
        postReportEntity.setDescription(requestBody.getDescription());
        postReportEntity.setHandled(false);
        postReportEntity.setDeleted(false);
        postReportEntity.setHandledBy(null);

        postReportEntity = postReportRepository.save(postReportEntity);
        return new PostReportDetailResponse(postReportEntity.getId(), convertPostEntityToPostDescriptionForAdminBusiness(postReportEntity.getPost()),
                postReportEntity.getTypeReport().getName(), postReportEntity.getEmailReport(), postReportEntity.getPhoneReport(),
                postReportEntity.getDescription(), postReportEntity.getHandled(), null);
    }

    @Override
    public List<PostReportDescriptionResponse> getPostReportList(Integer page, Integer size, String order, Long postId, Long typeId, Long userId, Boolean viewed, Boolean handled) {
        if(order != null && order.equalsIgnoreCase("DATEDESC")){
            order = order.toUpperCase();
        }else{
            order = null;
        }

        Integer viewedInt, handledInt;

        if(viewed == null){
            viewedInt = null;
        }else {
            viewedInt = viewed ? 1 : 0;
        }

        if(handled == null){
            handledInt = null;
        }else {
            handledInt = handled ? 1 : 0;
        }

        Pageable pageable = PageRequest.of(page, size);
        List<PostReportEntity> postReportEntityList
                = postReportRepository.findAllWithFilter(order, postId, typeId, userId, viewedInt, handledInt, pageable);

        if(postReportEntityList.isEmpty()){
            return Collections.emptyList();
        }

        return postReportEntityList.stream()
                .map(o -> new PostReportDescriptionResponse(o.getId(), o.getTypeReport().getName(), o.getEmailReport(),
                        o.getPhoneReport(), o.getHandled(), convertEntityToDescriptionReview(o.getHandledBy())
                        )).collect(Collectors.toList());
    }

    @Override
    public PostReportDetailResponse getPostReportDetail(Long id) {
        PostReportEntity postReportEntity = postReportRepository.findOneByIdAndDeletedFalse(id);
        if(postReportEntity == null){
            throw new AppException("Không tìm thấy báo cáo với id: " + id);
        }
        return new PostReportDetailResponse(postReportEntity.getId(), convertPostEntityToPostDescriptionForAdminBusiness(postReportEntity.getPost()),
                postReportEntity.getTypeReport().getName(), postReportEntity.getEmailReport(), postReportEntity.getPhoneReport(), postReportEntity.getDescription(),
                postReportEntity.getHandled(), convertEntityToDescriptionReview(postReportEntity.getHandledBy()));
    }

    @Override
    public void changeHandledState(Long id, Boolean handled) {
        PostReportEntity postReportEntity = postReportRepository.findOneByIdAndDeletedFalse(id);
        if(postReportEntity == null){
            throw new AppException("Không tìm thấy báo cáo với id: " + id);
        }

        if(postReportEntity.getHandled()){
            throw new AppException("Báo cáo đã được xử lý");
        }

        if(!handled){
            throw new AppException("Thay đổi trạng thái không thành công");
        }

        postReportEntity.setHandled(true);
        postReportEntity.setHandledBy(getCurrentUserEntity());
        postReportRepository.save(postReportEntity);
    }


    @Override
    @Transactional
    public void deletePostReport(List<Long> ids) {
        if(ids == null || ids.isEmpty()){
            throw new AppException("Xoá không thành công");
        }
        List<PostReportEntity> postReportEntityList = postReportRepository.findAllByIdInAndDeletedFalse(ids);
        if(postReportEntityList.size() != ids.size()){
            throw new AppException("Xoá không thành công");
        }

        for(PostReportEntity i : postReportEntityList){
            i.setDeleted(true);
            postReportRepository.save(i);
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
        if(reviewEntityList.isEmpty() || !userEntity.getDisplayReview()){
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
        List<PostImageEntity> postImageEntityList = postImageRepository.findAllByPostIdAndDeletedFalse(postEntity.getId());
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

    private PostDescriptionForAdminBusinessResponse convertPostEntityToPostDescriptionForAdminBusiness(PostEntity postEntity){
        PostImageEntity postImageEntity = postImageRepository.findAllByPostIdAndDeletedFalseAndMainImageFalse(postEntity.getId());
        return new PostDescriptionForAdminBusinessResponse(postEntity.getId(), postEntity.getTitle(), postEntity.getTypeEstate().getName(),
                postEntity.getWards().getDistrict().getProvince().getName(), postEntity.getWards().getDistrict().getName(),
                postEntity.getWards().getName(), postEntity.getExpiredDate(), postEntity.getDeleted(), postEntity.getHide(), postEntity.getLocked(),
                postEntity.getVerified(), postEntity.getCreatedBy().getFirstName() + " " + postEntity.getCreatedBy().getLastName(),
                postImageEntity == null ? null : postImageEntity.getUrl(), postEntity.getCreatedDate());
    }

    private List<PostImageResponse> getPostImageListByPostId(Long id){
        List<PostImageEntity> postImageEntityList = postImageRepository.findAllByPostIdAndDeletedFalse(id);
        if(postImageEntityList.isEmpty()){
            return Collections.emptyList();
        }
        return postImageEntityList.stream()
                    .map(o -> new PostImageResponse(o.getId(), o.getUrl(), o.getMainImage())).collect(Collectors.toList());
    }

    private UserDescriptionReviewResponse convertEntityToDescriptionReview(UserEntity userEntity){
        if(userEntity == null){
            return null;
        }
        return new UserDescriptionReviewResponse(userEntity.getId(), userEntity.getUsername(), userEntity.getFirstName(),
                userEntity.getLastName(), userEntity.getImageUrl());
    }
}
