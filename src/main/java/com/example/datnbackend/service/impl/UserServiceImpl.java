package com.example.datnbackend.service.impl;

import com.example.datnbackend.dto.exception.AppException;
import com.example.datnbackend.dto.security.*;
import com.example.datnbackend.dto.user.UserDescriptionAdminResponse;
import com.example.datnbackend.dto.user.UserDetailRequest;
import com.example.datnbackend.dto.user.UserDetailResponse;
import com.example.datnbackend.entity.RoleEntity;
import com.example.datnbackend.entity.UserEntity;
import com.example.datnbackend.entity.WardsEntity;
import com.example.datnbackend.repository.RoleRepository;
import com.example.datnbackend.repository.UserRepository;
import com.example.datnbackend.repository.WardsRepository;
import com.example.datnbackend.security.JwtTokenProvider;
import com.example.datnbackend.security.UserPrincipal;
import com.example.datnbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    WardsRepository wardsRepository;

    @Override
    public ResponseEntity<?> signin(UserSigninRequest signinDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signinDTO.getUsername(),
                        signinDTO.getPassword()
                )
        );

        if(userRepository.findByUsernameWithLockedIsFalse(signinDTO.getUsername()) == null){
            return new ResponseEntity<>(new SecurityResponse(false, "Your account is locked"), HttpStatus.FORBIDDEN);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @Override
    public SecurityResponse signup(UserSignupRequest signupDTO) {
        RoleEntity userRole;
        checkDuplicateField(signupDTO.getUsername(), signupDTO.getEmail(), signupDTO.getPhone());

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(signupDTO.getUsername());
        userEntity.setPassword(passwordEncoder.encode(signupDTO.getPassword()));
        userEntity.setFirstName(signupDTO.getFirstName());
        userEntity.setLastName(signupDTO.getLastName());
        userEntity.setBirthDay(signupDTO.getBirthDay());
        userEntity.setEmail(signupDTO.getEmail());
        userEntity.setPhone(signupDTO.getPhone());
        userEntity.setDeleted(false);
        userEntity.setLocked(false);

        if(signupDTO.getRole().equalsIgnoreCase("BUSINESS")){
            userRole = roleRepository.findByName(RoleEntity.Name.ROLE_BUSINESS);
            if(userRole == null){
                return new SecurityResponse(false, "User role not exists");
            }
            userEntity.setDisplayReview(true);
        }else if(signupDTO.getRole().equalsIgnoreCase("CUSTOMER")){
            userRole = roleRepository.findByName(RoleEntity.Name.ROLE_CUSTOMER);
            if(userRole == null){
                return new SecurityResponse(false, "User role not exists");
            }
            userEntity.setDisplayReview(false);
        }else {
            return new SecurityResponse(false, "User role not exists");
        }

        WardsEntity wardsEntity = wardsRepository.findOneById(signupDTO.getWardsId());
        if(wardsEntity == null){
            return new SecurityResponse(false, "Not found address with id: " + signupDTO.getWardsId());
        }
        userEntity.setWards(wardsEntity);

        userEntity.setRoles(Collections.singleton(userRole));

        userRepository.save(userEntity);

        return new SecurityResponse(true, "Registered successfully");
    }

    @Override
    public SecurityResponse signupAdmin(UserSignupAdminRequest signupDTO) {
        RoleEntity userRole;

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(signupDTO.getUsername());
        userEntity.setPassword(passwordEncoder.encode(signupDTO.getPassword()));
        userEntity.setFirstName(signupDTO.getFirstName());
        userEntity.setLastName(signupDTO.getLastName());
        userEntity.setBirthDay(signupDTO.getBirthDay());
        userEntity.setEmail(signupDTO.getEmail());
        userEntity.setPhone(signupDTO.getPhone());
        userEntity.setDeleted(false);
        userEntity.setLocked(false);

        if(signupDTO.getWardsId() != null){
            WardsEntity wardsEntity = wardsRepository.findOneById(signupDTO.getWardsId());
            if(wardsEntity == null){
                return new SecurityResponse(false, "Not found address with id: " + signupDTO.getWardsId());
            }
            userEntity.setWards(wardsEntity);
        }else {
            userEntity.setWards(null);
        }

        userRole = roleRepository.findByName(RoleEntity.Name.ROLE_ADMIN);
        if(userRole == null){
            return new SecurityResponse(false, "User role not exists");
        }
        userEntity.setDisplayReview(false);

        userEntity.setRoles(Collections.singleton(userRole));

        userRepository.save(userEntity);

        return new SecurityResponse(true, "Registered successfully");
    }

    @Override
    public List<UserDescriptionAdminResponse> getUserDescriptionForAdmin(Integer page, Integer size, String query) {
        List<UserDescriptionAdminResponse> userDescriptionAdminResponseList;
        List<UserEntity> userEntityList;
        if(page != null && size != null){
            Pageable pageable = PageRequest.of(page, size);
            userEntityList = userRepository.findAllUserWithPagingAndQueryAndDeletedIsFalse(query, pageable);
        }else {
            userEntityList = userRepository.findAllUserWithDeletedIsFalseWithNormalRole();
        }

        if(userEntityList.isEmpty()){
            return Collections.emptyList();
        }

        userDescriptionAdminResponseList = userEntityList.stream().map(o -> new UserDescriptionAdminResponse(
                o.getId(), o.getUsername(), o.getDisplayReview(), o.getLocked()
        )).collect(Collectors.toList());

        return userDescriptionAdminResponseList;
    }

    @Override
    public UserDetailResponse getUserDetail(Long id) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userPrincipal.getId() != id){
            throw new AppException("You do not have role");
        }

        UserEntity userEntity = userRepository.findOneByIdAndDeletedFalse(id);
        if(userEntity == null){
            throw new AppException("Not found user with id: " + id);
        }
        return new UserDetailResponse(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getBirthDay(),
                userEntity.getPhone(),
                userEntity.getEmail(),
                userEntity.getWards() == null ? null : userEntity.getWards().getDistrict().getProvince().getName(),
                userEntity.getWards() == null ? null : userEntity.getWards().getDistrict().getName(),
                userEntity.getWards() == null ? null : userEntity.getWards().getName(),
                null,
                userEntity.getDisplayReview(),
                userEntity.getLocked(),
                userEntity.getCreatedDate());
    }

    @Override
    public UserDetailResponse getUserDetailForAdmin(Long id) {
        UserEntity userEntity = userRepository.findOneByIdAndDeletedFalseWithNormalRole(id);
        if(userEntity == null){
            throw new AppException("Not found user with id: " + id);
        }
        return new UserDetailResponse(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getBirthDay(),
                userEntity.getPhone(),
                userEntity.getEmail(),
                userEntity.getWards() == null ? null : userEntity.getWards().getDistrict().getProvince().getName(),
                userEntity.getWards() == null ? null : userEntity.getWards().getDistrict().getName(),
                userEntity.getWards() == null ? null : userEntity.getWards().getName(),
                null,
                userEntity.getDisplayReview(),
                userEntity.getLocked(),
                userEntity.getCreatedDate());
    }


    @Override
    public UserDetailResponse updateUserDetail(Long id, UserDetailRequest requestBody) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(userPrincipal.getId() != id){
            throw new AppException("You do not have role");
        }

        UserEntity userEntity = userRepository.findOneByIdAndDeletedFalse(id);
        if(userEntity == null){
            throw new AppException("Not found user with id: " + id);
        }

        if(requestBody.getUsername() != null){
            if(userRepository.existsByUsername(requestBody.getUsername())){
                throw new AppException("Username already exits");
            }
            userEntity.setUsername(requestBody.getUsername());
        }
        if(requestBody.getEmail() != null){
            if(userRepository.findByEmail(requestBody.getEmail()) != null){
                throw new AppException("Email already exits");
            }
            userEntity.setEmail(requestBody.getEmail());
        }
        if(requestBody.getPhone() != null){
            if(userRepository.findByPhone(requestBody.getPhone()) != null){
                throw new AppException("Phone number already exits");
            }
            userEntity.setPhone(requestBody.getPhone());
        }
        if(requestBody.getFirstName() != null){
            userEntity.setFirstName(requestBody.getFirstName());
        }
        if(requestBody.getLastName() != null){
            userEntity.setLastName(requestBody.getLastName());
        }
        if(requestBody.getBirthDay() != null){
            userEntity.setBirthDay(requestBody.getBirthDay());
        }
        if(requestBody.getWardsId() != null){
            WardsEntity wardsEntity = wardsRepository.findOneById(requestBody.getWardsId());
            if(wardsEntity == null){
                throw new AppException("Not found address with id: " + requestBody.getWardsId());
            }
            userEntity.setWards(wardsEntity);
        }

        userRepository.save(userEntity);

        return new UserDetailResponse(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getBirthDay(),
                userEntity.getPhone(),
                userEntity.getEmail(),
                userEntity.getWards() == null ? null : userEntity.getWards().getDistrict().getProvince().getName(),
                userEntity.getWards() == null ? null : userEntity.getWards().getDistrict().getName(),
                userEntity.getWards() == null ? null : userEntity.getWards().getName(),
                null,
                userEntity.getDisplayReview(),
                userEntity.getLocked(),
                userEntity.getCreatedDate());
    }

    @Override
    public void lockUserAccount(Long id, Boolean locked) {
        UserEntity userEntity = userRepository.findOneByIdAndDeletedFalseWithNormalRole(id);
        if(userEntity == null){
            throw new AppException("Not found user with id: " + id);
        }
        if(locked){
            userEntity.setLocked(true);
        }else{
            userEntity.setLocked(false);
        }
        userRepository.save(userEntity);
    }

    @Override
    public void displayReviewUserAccount(Long id, Boolean display) {
        UserEntity userEntity = userRepository.findOneByIdAndDeletedFalseWithNormalRole(id);
        if(userEntity == null){
            throw new AppException("Not found user with id: " + id);
        }
        if(display){
            userEntity.setDisplayReview(true);
        }else{
            userEntity.setDisplayReview(false);
        }
        userRepository.save(userEntity);
    }

    @Override
    @Transactional
    public void deleteMultipleUser(List<Long> ids) {
        for(Long id : ids){
            UserEntity userEntity = userRepository.findOneByIdAndDeletedFalseWithNormalRole(id);
            if(userEntity == null){
                throw new AppException("Not found user with id: " + id);
            }
            userEntity.setDeleted(true);
            userRepository.save(userEntity);
        }
    }


    private Boolean checkAuthorities(List<String> compareAuthorities, UserPrincipal userPrincipal){
        for(String i : compareAuthorities){
            if(userPrincipal.getAuthorities().contains(new SimpleGrantedAuthority(i))){
                return true;
            }
        }
        return false;
    }

    private void checkDuplicateField(String username, String email, String phone){
        if(username != null && userRepository.existsByUsername(username)){
            throw new AppException("Username already exits");
        }

        if(email != null && userRepository.findByEmail(email) != null){
            throw new AppException("Email already exits");
        }

        if(phone != null && userRepository.findByPhone(phone) != null){
            throw new AppException("Phone number already exits");
        }
    }
}
