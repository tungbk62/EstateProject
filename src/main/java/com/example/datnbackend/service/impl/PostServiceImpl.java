package com.example.datnbackend.service.impl;

import com.example.datnbackend.dto.exception.AppException;
import com.example.datnbackend.dto.post.*;
import com.example.datnbackend.dto.type.TypePostResponse;
import com.example.datnbackend.dto.user.UserDescriptionPostDetailResponse;
import com.example.datnbackend.dto.user.UserDescriptionReviewResponse;
import com.example.datnbackend.entity.*;
import com.example.datnbackend.repository.*;
import com.example.datnbackend.security.UserPrincipal;
import com.example.datnbackend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
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
    TypePostRepository typePostRepository;

    @Value("${image.post.default}")
    private String imageDefaultUrl;
    @Value("${image.avatar.default}")
    private String avatarDefaultUrl;

    @Override
    public PostDetailForBusinessResponse createPost(PostCreateRequest requestBody) {
        if(requestBody.getTitle() == null || requestBody.getTitle().isEmpty()){
            throw new AppException("Title kh??ng ???????c null ho???c tr???ng");
        }
        if(requestBody.getDescription() == null || requestBody.getDescription().isEmpty()){
            throw new AppException("Ph???n m?? t??? kh??ng ???????c null ho???c tr???ng");
        }
        if(requestBody.getWardsId() == null){
            throw new AppException("?????a ch??? kh??ng ???????c null");
        }
        if(requestBody.getPriceMonth() == null){
            throw new AppException("Gi?? kh??ng ???????c null");
        }
        if(requestBody.getArea() == null){
            throw new AppException("Di???n t??ch kh??ng ???????c null");
        }

        PostEntity postEntity = new PostEntity();
        postEntity.setTitle(requestBody.getTitle());
        postEntity.setDescription(requestBody.getDescription());

        if(requestBody.getTypeEstateId() != null){
            TypeEstateEntity typeEstateEntity = typeEstateRepository.findOneById(requestBody.getTypeEstateId());
            if(typeEstateEntity == null){
                throw new AppException("Kh??ng t??m th???y ki???u b???t ?????ng s???n");
            }
            postEntity.setTypeEstate(typeEstateEntity);
        }

        WardsEntity wardsEntity = wardsRepository.findOneById(requestBody.getWardsId());
        if(wardsEntity == null){
            throw new AppException("Kh??ng t??m th???y ?????a ch???");
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
                throw new AppException("Expired date ph???i sau th???i gian hi???n t???i");
            }
        }
        postEntity.setExpiredDate(requestBody.getExpiredDate());

        Long typePostId = requestBody.getTypePostId();
        TypePostEntity typePostEntity = typePostRepository.findOneById(typePostId == null ? 4 : typePostId);
        if(typePostEntity == null){
            throw new AppException("Kh??ng t??m th???y lo???i b??i ????ng");
        }

        postEntity.setTypePost(typePostEntity);

        postEntity = postRepository.save(postEntity);


        return new PostDetailForBusinessResponse(postEntity.getId(), postEntity.getTitle(), postEntity.getDescription(),
                postEntity.getTypeEstate().getName(), postEntity.getWards().getDistrict().getProvince().getName(),
                postEntity.getWards().getDistrict().getName(), postEntity.getWards().getName(), postEntity.getAddressDetail(),
                postEntity.getArea(), postEntity.getPriceMonth(), postEntity.getFurniture(), postEntity.getRoom(),
                postEntity.getBathRoom(), postEntity.getExpiredDate(), postEntity.getDeleted(), postEntity.getHide(),
                postEntity.getLocked(), postEntity.getVerified(), postEntity.getView(),
                postEntity.getTypePost().getName(),getPostImageListByPostId(postEntity.getId()),
                convertEntityToDescriptionPostDetailResponse(postEntity.getCreatedBy()), postEntity.getCreatedDate(),
                postEntity.getModifiedDate());
    }

    @Override
    public PostDetailForBusinessResponse updatePost(Long id, PostCreateRequest requestBody) {
        PostEntity postEntity = postRepository.findOneByIdAndDeletedFalseAndLockedFalse(id);

        if(postEntity == null){
            throw new AppException("Kh??ng t??m th???y b??i ????ng v???i id: " + id);
        }

        if(!getCurrentUserEntity().getId().equals(postEntity.getCreatedBy().getId())){
            throw new AppException("Kh??ng c?? quy???n ch???nh s???a b??i ????ng n??y");
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
                throw new AppException("Kh??ng t??m th???y ki???u b???t ?????ng s???n");
            }
            postEntity.setTypeEstate(typeEstateEntity);
        }
        if(requestBody.getWardsId() != null){
            WardsEntity wardsEntity = wardsRepository.findOneById(requestBody.getWardsId());
            if(wardsEntity == null){
                throw new AppException("Kh??ng t??m th???y ?????a ch???");
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
                throw new AppException("Expired date ph???i sau th???i gian hi???n t???i ??t nh???t 1 ti???ng");
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
                postEntity.getLocked(), postEntity.getVerified(), postEntity.getView(),
                postEntity.getTypePost().getName(),
                getPostImageListByPostId(postEntity.getId()),
                convertEntityToDescriptionPostDetailResponse(postEntity.getCreatedBy()), postEntity.getCreatedDate(),
                postEntity.getModifiedDate());
    }

    @Override
    public List<PostDescriptionResponse> getPostDescriptionList(Integer page, Integer size, String order, String search,
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

        if(search == null || search.isEmpty()){
            search = null;
        }else {
            search = search.toUpperCase();
        }

        List<PostEntity> postEntityList = postRepository.findAllWithFilterWithDeletedFalseAndHideFalseAndLockedFalse(order, search, province, district, wards,
                 type, room, pricemin, pricemax, areamin, areamax, pageable);

        if(postEntityList.isEmpty()){
            return Collections.emptyList();
        }
        return postEntityList.stream()
                .map(o -> convertPostEntityToPostDescription(o)).collect(Collectors.toList());
    }


    @Override
    public List<PostDescriptionForAdminBusinessResponse> getPostDescriptionListForBusiness(Integer page, Integer size, Long typePostId) {
        UserEntity currentUser = getCurrentUserEntity();
        Pageable pageable = PageRequest.of(page, size);
        List<PostEntity> postEntityList = postRepository.findAllByCreatedByIdAndDeletedFalse(currentUser.getId(), typePostId, pageable);
        if(postEntityList.isEmpty()){
            return Collections.emptyList();
        }
        return postEntityList.stream()
                .map(o -> convertPostEntityToPostDescriptionForAdminBusiness(o)).collect(Collectors.toList());
    }

    @Override
    public List<PostDescriptionForAdminBusinessResponse> getPostDescriptionListForAdmin(Integer page, Integer size, Long userId, Long typePostId) {
        Pageable pageable = PageRequest.of(page, size);
        List<PostEntity> postEntityList = postRepository.findAllByDeletedFalse(userId, typePostId, pageable);
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
            throw new AppException("Kh??ng t??m th???y b??i ????ng v???i id: " + id);
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
            throw new AppException("Kh??ng t??m th???y b??i ????ng v???i id: " + id);
        }

        return new PostDetailForAdminResponse(postEntity.getId(), postEntity.getTitle(), postEntity.getDescription(),
                postEntity.getTypeEstate().getName(), postEntity.getWards().getDistrict().getProvince().getName(),
                postEntity.getWards().getDistrict().getName(), postEntity.getWards().getName(), postEntity.getAddressDetail(),
                postEntity.getArea(), postEntity.getPriceMonth(), postEntity.getFurniture(), postEntity.getRoom(),
                postEntity.getBathRoom(), postEntity.getExpiredDate(), postEntity.getDeleted(), postEntity.getHide(),
                postEntity.getLocked(), postEntity.getVerified(), postEntity.getView(),
                postEntity.getTypePost().getName(),
                getPostImageListByPostId(postEntity.getId()),
                convertEntityToDescriptionPostDetailResponse(postEntity.getCreatedBy()), postEntity.getCreatedDate(),
                postEntity.getModifiedDate());
    }

    @Override
    public PostDetailForBusinessResponse getPostDetailForBusiness(Long id) {
        UserEntity userEntity = getCurrentUserEntity();

        PostEntity postEntity = postRepository.findOneByIdAndDeletedFalse(id);
        if(postEntity == null){
            throw new AppException("Kh??ng t??m th???y b??i ????ng v???i id: " + id);
        }

        if(!userEntity.getId().equals(postEntity.getCreatedBy().getId())){
            throw new AppException("Kh??ng c?? quy???n truy c???p b??i ????ng n??y");
        }
        return new PostDetailForBusinessResponse(postEntity.getId(), postEntity.getTitle(), postEntity.getDescription(),
                postEntity.getTypeEstate().getName(), postEntity.getWards().getDistrict().getProvince().getName(),
                postEntity.getWards().getDistrict().getName(), postEntity.getWards().getName(), postEntity.getAddressDetail(),
                postEntity.getArea(), postEntity.getPriceMonth(), postEntity.getFurniture(), postEntity.getRoom(),
                postEntity.getBathRoom(), postEntity.getExpiredDate(), postEntity.getDeleted(), postEntity.getHide(),
                postEntity.getLocked(), postEntity.getVerified(), postEntity.getView(),
                postEntity.getTypePost().getName(),
                getPostImageListByPostId(postEntity.getId()),
                convertEntityToDescriptionPostDetailResponse(postEntity.getCreatedBy()), postEntity.getCreatedDate(),
                postEntity.getModifiedDate());
    }


    @Override
    public void hidePost(Long id, Boolean hide) {
        PostEntity postEntity = postRepository.findOneByIdAndDeletedFalseAndLockedFalse(id);
        if(postEntity == null){
            throw new AppException("Kh??ng t??m th???y b??i ????ng v???i id: " + id);
        }

        if(!getCurrentUserEntity().getId().equals(postEntity.getCreatedBy().getId())){
            throw new AppException("Kh??ng c?? quy???n ch???nh s???a b??i ????ng n??y");
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
                throw new AppException("C?? id kh??ng t??m th???y");
            }
            if(!id.equals(postEntity.getCreatedBy().getId())){
                throw new AppException("C?? b??i vi???t kh??ng c?? quy???n truy c???p");
            }
            postEntity.setDeleted(true);
            postRepository.save(postEntity);
        }
    }

    @Override
    public void changeStateOfHideLockedVerified(Long id, Boolean hide, Boolean locked, Boolean verified) {
        PostEntity postEntity = postRepository.findOneByIdAndDeletedFalse(id);
        if(postEntity == null){
            throw new AppException("Kh??ng t??m th???y b??i ????ng v???i id: " + id);
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

        List<PostEntity> postEntityList = currentUser.getPostSave();
        postEntityList.add(postEntity);
        currentUser.setPostSave(postEntityList);
        userRepository.save(currentUser);
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
            throw new AppException("Xo?? kh??ng th??nh c??ng");
        }
        UserEntity userEntity = getCurrentUserEntity();
        List<PostEntity> postEntityList = postRepository.findAllByUserIdNoPaging(userEntity.getId(), ids);

        if(postEntityList.size() != ids.size()){
            throw new AppException("C?? id kh??ng t??m th???y");
        }

        postRepository.deleteByUserIdAndPostIdIn(userEntity.getId(), ids);
    }


    @Override
    public PostReportDetailResponse createPostReport(Long id, PostReportCreateRequest requestBody) {
        if(requestBody.getTypeReportId() == null){
            throw new AppException("Ph???i ch???n lo???i b??o c??o b??i ????ng");
        }
        if(requestBody.getEmailReport() == null && requestBody.getPhoneReport() == null){
            throw new AppException("C???n ??i???n th??ng tin email ho???c s??? ??i???n tho???i");
        }

        if(requestBody.getEmailReport() != null && !isValidEmail(requestBody.getEmailReport())){
            throw new AppException("?????a ch??? email kh??ng ????ng");
        }

        if(requestBody.getPhoneReport() != null && requestBody.getPhoneReport().isEmpty()){
            throw new AppException("S??? ??i???n tho???i kh??ng ????ng");
        }

        PostEntity postEntity = postRepository.findOneByIdAndDeletedFalseAndHideFalseAndLockedFalse(id);
        if(postEntity == null){
            throw new AppException("Kh??ng t??m th???y b??i ????ng v???i id: " + id);
        }

        TypeReportEntity typeReportEntity = typeReportRepository.findOneById(requestBody.getTypeReportId());
        if(typeReportEntity == null){
            throw new AppException("Kh??ng t??m th???y b??o c??o b??i ????ng v???i id: " + requestBody.getTypeReportId());
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
                postReportEntity.getDescription(), postReportEntity.getHandled(), null, postReportEntity.getCreatedDate());
    }

    @Override
    public List<PostReportDescriptionResponse> getPostReportList(Integer page, Integer size, String order, Long postId, Long typeId, Long userId, Boolean handled) {
        if(order != null && order.equalsIgnoreCase("DATEDESC")){
            order = order.toUpperCase();
        }else{
            order = null;
        }

        Integer handledInt;

        if(handled == null){
            handledInt = null;
        }else {
            handledInt = handled ? 1 : 0;
        }

        Pageable pageable = PageRequest.of(page, size);
        List<PostReportEntity> postReportEntityList
                = postReportRepository.findAllWithFilter(order, postId, typeId, userId, handledInt, pageable);

        if(postReportEntityList.isEmpty()){
            return Collections.emptyList();
        }

        return postReportEntityList.stream()
                .map(o -> new PostReportDescriptionResponse(o.getId(), o.getTypeReport().getName(), o.getEmailReport(),
                        o.getPhoneReport(), o.getHandled(), convertEntityToDescriptionReview(o.getHandledBy()), o.getCreatedDate()
                        )).collect(Collectors.toList());
    }

    @Override
    public PostReportDetailResponse getPostReportDetail(Long id) {
        PostReportEntity postReportEntity = postReportRepository.findOneByIdAndDeletedFalse(id);
        if(postReportEntity == null){
            throw new AppException("Kh??ng t??m th???y b??o c??o v???i id: " + id);
        }
        return new PostReportDetailResponse(postReportEntity.getId(), convertPostEntityToPostDescriptionForAdminBusiness(postReportEntity.getPost()),
                postReportEntity.getTypeReport().getName(), postReportEntity.getEmailReport(), postReportEntity.getPhoneReport(), postReportEntity.getDescription(),
                postReportEntity.getHandled(), convertEntityToDescriptionReview(postReportEntity.getHandledBy()), postReportEntity.getCreatedDate());
    }

    @Override
    public void changeHandledState(Long id, Boolean handled) {
        PostReportEntity postReportEntity = postReportRepository.findOneByIdAndDeletedFalse(id);
        if(postReportEntity == null){
            throw new AppException("Kh??ng t??m th???y b??o c??o v???i id: " + id);
        }

        if(postReportEntity.getHandled()){
            throw new AppException("B??o c??o ???? ???????c x??? l??");
        }

        if(!handled){
            throw new AppException("Thay ?????i tr???ng th??i kh??ng th??nh c??ng");
        }

        postReportEntity.setHandled(true);
        postReportEntity.setHandledBy(getCurrentUserEntity());
        postReportRepository.save(postReportEntity);
    }


    @Override
    @Transactional
    public void deletePostReport(List<Long> ids) {
        if(ids == null || ids.isEmpty()){
            throw new AppException("Xo?? kh??ng th??nh c??ng");
        }
        List<PostReportEntity> postReportEntityList = postReportRepository.findAllByIdInAndDeletedFalse(ids);
        if(postReportEntityList.size() != ids.size()){
            throw new AppException("Xo?? kh??ng th??nh c??ng");
        }

        for(PostReportEntity i : postReportEntityList){
            i.setDeleted(true);
            postReportRepository.save(i);
        }
    }

    @Override
    public List<TypePostResponse> getAllTypePost() {
        List<TypePostEntity> typePostEntityList = typePostRepository.findAll();
        if(typePostEntityList.isEmpty()){
            return Collections.emptyList();
        }
        return typePostEntityList.stream().map(o -> new TypePostResponse(o.getId(), o.getName())).collect(Collectors.toList());
    }

    @Override
    public void changeTypePost(Long id, Long typePostId) {
        if(typePostId == null){
            throw new AppException("Ph???i ch???n ki???u b??i ????ng");
        }
        UserEntity currentUser = getCurrentUserEntity();
        TypePostEntity typePostEntity = typePostRepository.findOneById(typePostId);
        if(typePostEntity == null){
            throw new AppException("Kh??ng t??m th???y ki???u b??i ????ng");
        }

        PostEntity postEntity = postRepository.findOneByIdAndDeletedFalseAndLockedFalse(id);
        if(postEntity == null){
            throw new AppException("Kh??ng t??m th???y b??i ????ng");
        }
        if(!postEntity.getCreatedBy().getId().equals(currentUser.getId())){
            throw new AppException("Kh??ng c?? quy???n ch???nh s???a b??i ????ng n??y");
        }

        postEntity.setTypePost(typePostEntity);
        postRepository.save(postEntity);
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
        userDescriptionPostDetailResponse.setEmail(userEntity.getEmail());
        userDescriptionPostDetailResponse.setFirstName(userEntity.getFirstName());
        userDescriptionPostDetailResponse.setLastName(userEntity.getLastName());
        userDescriptionPostDetailResponse.setPhone(userEntity.getPhone());
        userDescriptionPostDetailResponse.setImageUrl(userEntity.getImageUrl() == null ? avatarDefaultUrl : userEntity.getImageUrl());
        userDescriptionPostDetailResponse.setType(returnTypeOfUser(userEntity));
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
                postImageEntityList.isEmpty() ? 1 : postImageEntityList.size(),  mainImageEntity == null ? imageDefaultUrl : mainImageEntity.getUrl(),
                postEntity.getCreatedDate());
    }

    private PostDescriptionForAdminBusinessResponse convertPostEntityToPostDescriptionForAdminBusiness(PostEntity postEntity){
        List<PostImageEntity> postImageEntityList = postImageRepository.findAllByPostIdAndDeletedFalse(postEntity.getId());
        PostImageEntity mainImageEntity;
        if(!postImageEntityList.isEmpty()){
            mainImageEntity = postImageEntityList.stream().filter(o -> o.getMainImage() == true).findFirst().get();
        }else {
            mainImageEntity = null;
        }
        return new PostDescriptionForAdminBusinessResponse(postEntity.getId(), postEntity.getTitle(), postEntity.getTypeEstate().getName(),
                postEntity.getWards().getDistrict().getProvince().getName(), postEntity.getWards().getDistrict().getName(),
                postEntity.getWards().getName(), postEntity.getExpiredDate(), postEntity.getDeleted(), postEntity.getHide(), postEntity.getLocked(),
                postEntity.getVerified(), postEntity.getCreatedBy().getFirstName() + " " + postEntity.getCreatedBy().getLastName(),
                postImageEntityList.isEmpty() ? 1 : postImageEntityList.size(), postEntity.getTypePost().getName(),
                mainImageEntity == null ? imageDefaultUrl : mainImageEntity.getUrl(), postEntity.getCreatedDate());
    }

    private List<PostImageResponse> getPostImageListByPostId(Long id){
        List<PostImageEntity> postImageEntityList = postImageRepository.findAllByPostIdAndDeletedFalse(id);
        if(postImageEntityList.isEmpty()){
            List<PostImageResponse> postImageResponseList = new ArrayList<>();
            postImageResponseList.add(new PostImageResponse(null, imageDefaultUrl, true));
            return postImageResponseList;
        }
        return postImageEntityList.stream()
                    .map(o -> new PostImageResponse(o.getId(), o.getUrl(), o.getMainImage())).collect(Collectors.toList());
    }

    private UserDescriptionReviewResponse convertEntityToDescriptionReview(UserEntity userEntity){
        if(userEntity == null){
            return null;
        }
        return new UserDescriptionReviewResponse(userEntity.getId(), userEntity.getEmail(), userEntity.getFirstName(),
                userEntity.getLastName(), userEntity.getImageUrl(), returnTypeOfUser(userEntity));
    }

    private Boolean isValidEmail(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pattern = Pattern.compile(emailRegex);
        if (email == null || email.isEmpty()){
            return false;
        }
        return pattern.matcher(email).matches();
    }

    private String returnTypeOfUser(UserEntity userEntity){
        if(userEntity == null){
            return null;
        }
        String role = userEntity.getRoles().stream().findFirst().get().getName().toString();
        if(role.equalsIgnoreCase("ROLE_BUSINESS")){
            return "BUSINESS";
        }else if(role.equalsIgnoreCase("ROLE_CUSTOMER")){
            return  "CUSTOMER";
        }else if(role.equalsIgnoreCase("ROLE_ADMIN")){
            return "ADMIN";
        }else {
            return null;
        }
    }
}
