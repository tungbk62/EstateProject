package com.example.datnbackend.service.impl;

import com.example.datnbackend.dto.email.EmailTemplate;
import com.example.datnbackend.dto.exception.AppException;
import com.example.datnbackend.dto.security.*;
import com.example.datnbackend.dto.user.*;
import com.example.datnbackend.entity.RoleEntity;
import com.example.datnbackend.entity.UserEntity;
import com.example.datnbackend.entity.WardsEntity;
import com.example.datnbackend.repository.RoleRepository;
import com.example.datnbackend.repository.UserRepository;
import com.example.datnbackend.repository.WardsRepository;
import com.example.datnbackend.security.JwtTokenProvider;
import com.example.datnbackend.security.UserPrincipal;
import com.example.datnbackend.service.EmailService;
import com.example.datnbackend.service.ImageService;
import com.example.datnbackend.service.UserService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
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
    @Autowired
    ImageService imageService;
    @Autowired
    EmailService emailService;

    private final String avatarDefaultUrl = "link default avatar";
    private LoadingCache<String, String> cache;

    @Override
    public ResponseEntity<?> signin(UserSigninRequest signinDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signinDTO.getEmail(),
                        signinDTO.getPassword()
                )
        );

        UserEntity userEntity = userRepository.findByEmailWithLockedIsFalse(signinDTO.getEmail());

        if(userEntity == null){
            return new ResponseEntity<>(new SecurityResponse(false, "Your account is locked"), HttpStatus.FORBIDDEN);
        }

//        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, returnTypeOfUser(userEntity)));
    }

    @Override
    public void signup(UserSignupRequest signupDTO) {
        RoleEntity userRole;
        checkDuplicateField(signupDTO.getEmail(), signupDTO.getPhone());

        UserEntity userEntity = new UserEntity();

        if(!isValidEmail(signupDTO.getEmail())){
            throw new AppException("Email không được để trống");
        }
        userEntity.setEmail(signupDTO.getEmail());

        if(signupDTO.getPassword() == null || signupDTO.getPassword().isEmpty()){
            throw new AppException("Phải có mật khẩu");
        }
        userEntity.setPassword(passwordEncoder.encode(signupDTO.getPassword()));
        if(signupDTO.getFirstName() == null || signupDTO.getFirstName().isEmpty()){
            throw new AppException("Tên không được null hoặc trống");
        }
        userEntity.setFirstName(signupDTO.getFirstName());
        if(signupDTO.getLastName() == null || signupDTO.getLastName().isEmpty()){
            throw new AppException("Tên không được null hoặc trống");
        }
        userEntity.setLastName(signupDTO.getLastName());
        userEntity.setBirthDay(signupDTO.getBirthDay());
        userEntity.setPhone(signupDTO.getPhone());
        userEntity.setDeleted(false);
        userEntity.setLocked(false);

        if(signupDTO.getType().equalsIgnoreCase("BUSINESS")){
            userRole = roleRepository.findByName(RoleEntity.Name.ROLE_BUSINESS);
            if(userRole == null){
                throw new AppException("Không tồn tại role");
            }
            userEntity.setDisplayReview(true);
        }else if(signupDTO.getType().equalsIgnoreCase("CUSTOMER")){
            userRole = roleRepository.findByName(RoleEntity.Name.ROLE_CUSTOMER);
            if(userRole == null){
                throw new AppException("Không tồn tại role");
            }
            userEntity.setDisplayReview(false);
        }else {
            throw new AppException("Không tồn tại role");
        }

        WardsEntity wardsEntity = wardsRepository.findOneById(signupDTO.getWardsId());
        if(wardsEntity == null){
            throw new AppException("Không tìm thấy địa chỉ với id: " + signupDTO.getWardsId());
        }
        userEntity.setWards(wardsEntity);

        userEntity.setRoles(Collections.singleton(userRole));

        userRepository.save(userEntity);
    }

    @Override
    public void signupAdmin(UserSignupAdminRequest signupDTO) {
        RoleEntity userRole;

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(signupDTO.getEmail());
        userEntity.setPassword(passwordEncoder.encode(signupDTO.getPassword()));
        userEntity.setFirstName(signupDTO.getFirstName());
        userEntity.setLastName(signupDTO.getLastName());
        userEntity.setBirthDay(signupDTO.getBirthDay());
        userEntity.setPhone(signupDTO.getPhone());
        userEntity.setDeleted(false);
        userEntity.setLocked(false);

        if(signupDTO.getWardsId() != null){
            WardsEntity wardsEntity = wardsRepository.findOneById(signupDTO.getWardsId());
            if(wardsEntity == null){
                throw  new AppException("Not found address with id: " + signupDTO.getWardsId());
            }
            userEntity.setWards(wardsEntity);
        }else {
            userEntity.setWards(null);
        }

        userRole = roleRepository.findByName(RoleEntity.Name.ROLE_ADMIN);
        if(userRole == null){
            throw  new AppException("User role not exists");
        }
        userEntity.setDisplayReview(false);

        userEntity.setRoles(Collections.singleton(userRole));

        userRepository.save(userEntity);
    }

    @Override
    public List<UserDescriptionAdminResponse> getUserDescriptionForAdmin(Integer page, Integer size, String type, String query) {
        if(type != null && type.equalsIgnoreCase("BUSINESS")){
            type = "ROLE_BUSINESS";
        }else if(type != null && type.equalsIgnoreCase("CUSTOMER")){
            type = "ROLE_CUSTOMER";
        }else {
            type = null;
        }

        List<UserDescriptionAdminResponse> userDescriptionAdminResponseList;
        List<UserEntity> userEntityList;
        Pageable pageable = PageRequest.of(page, size);
        userEntityList = userRepository.findAllUserWithPagingAndQueryAndDeletedIsFalse(type, query, pageable);

        if(userEntityList.isEmpty()){
            return Collections.emptyList();
        }

        userDescriptionAdminResponseList = userEntityList.stream().map(o -> new UserDescriptionAdminResponse(
                o.getId(), o.getEmail(), o.getFirstName(), o.getLastName(), o.getDisplayReview(), o.getLocked(),
                returnTypeOfUser(o), o.getImageUrl() == null ? avatarDefaultUrl : o.getImageUrl())).collect(Collectors.toList());

        return userDescriptionAdminResponseList;
    }

    @Override
    public UserDetailResponse getUserDetail() {
        UserEntity userEntity = getCurrentUserEntity();
        return new UserDetailResponse(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getBirthDay(),
                userEntity.getPhone(),
                userEntity.getWards() == null ? null : userEntity.getWards().getDistrict().getProvince().getName(),
                userEntity.getWards() == null ? null : userEntity.getWards().getDistrict().getName(),
                userEntity.getWards() == null ? null : userEntity.getWards().getName(),
                userEntity.getImageUrl() == null ? avatarDefaultUrl : userEntity.getImageUrl(),
                userEntity.getDisplayReview(),
                userEntity.getCreatedDate(),
                returnTypeOfUser(userEntity));
    }

    @Override
    public UserDetailAdminResponseRequest getUserDetailForAdmin(Long id) {
        UserEntity userEntity = userRepository.findOneByIdAndDeletedFalseWithNormalRole(id);
        if(userEntity == null){
            throw new AppException("Not found user with id: " + id);
        }
        return new UserDetailAdminResponseRequest(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getBirthDay(),
                userEntity.getPhone(),
                userEntity.getWards() == null ? null : userEntity.getWards().getDistrict().getProvince().getName(),
                userEntity.getWards() == null ? null : userEntity.getWards().getDistrict().getName(),
                userEntity.getWards() == null ? null : userEntity.getWards().getName(),
                userEntity.getImageUrl() == null ? avatarDefaultUrl : userEntity.getImageUrl(),
                userEntity.getDisplayReview(),
                userEntity.getLocked(),
                userEntity.getDeleted(),
                userEntity.getCreatedDate(),
                returnTypeOfUser(userEntity));
    }


    @Override
    public UserDetailResponse updateUserDetail(UserDetailRequest requestBody) {
        UserEntity userEntity = getCurrentUserEntity();

        if(requestBody.getEmail() != null){
            if(userRepository.findByEmail(requestBody.getEmail()) != null){
                throw new AppException("Email đã tồn tại");
            }
            userEntity.setEmail(requestBody.getEmail());
        }
        if(requestBody.getPhone() != null){
            if(userRepository.findByPhone(requestBody.getPhone()) != null){
                throw new AppException("Số điện thoại đã tồn tại");
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
                throw new AppException("Không tìm thấy phường với id: " + requestBody.getWardsId());
            }
            userEntity.setWards(wardsEntity);
        }

        userRepository.save(userEntity);

        return new UserDetailResponse(
                userEntity.getId(),
                userEntity.getEmail(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getBirthDay(),
                userEntity.getPhone(),
                userEntity.getWards() == null ? null : userEntity.getWards().getDistrict().getProvince().getName(),
                userEntity.getWards() == null ? null : userEntity.getWards().getDistrict().getName(),
                userEntity.getWards() == null ? null : userEntity.getWards().getName(),
                userEntity.getImageUrl() == null ? null : userEntity.getImageUrl(),
                userEntity.getDisplayReview(),
                userEntity.getCreatedDate(),
                returnTypeOfUser(userEntity));
    }

    @Override
    public void lockUserAccount(Long id, Boolean locked) {
        UserEntity userEntity = userRepository.findOneByIdAndDeletedFalseWithNormalRole(id);
        if(userEntity == null){
            throw new AppException("Không tìm thấy user với id: " + id);
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
            throw new AppException("Không tìm thấy user với id: " + id);
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
                throw new AppException("Không tìm thấy user với id: " + id);
            }
            userEntity.setDeleted(true);
            userRepository.save(userEntity);
        }
    }

    @Override
    public void changePassword(ChangePasswordRequest requestBody) {
        if(requestBody.getOldPassword() == null || requestBody.getOldPassword().isEmpty()){
            throw new AppException("Mật khẩu không được null hoặc trống");
        }

        if(requestBody.getNewPassword() == null || requestBody.getNewPassword().isEmpty()){
            throw new AppException("Mật khẩu không được null hoặc trống");
        }

        UserEntity userEntity = getCurrentUserEntity();

        if(!passwordEncoder.matches(requestBody.getOldPassword(), userEntity.getPassword())){
            throw new AppException("Mật khẩu sai");
        }

        userEntity.setPassword(passwordEncoder.encode(requestBody.getNewPassword()));
        userRepository.save(userEntity);
    }

    @Override
    public void forgetPasswordRequest(ForgetPasswordRequest requestBody) {
        if(requestBody.getEmail() == null){
            throw new AppException("Không được null");
        }

        UserEntity userEntity = userRepository.findOneByEmailAndDeletedFalseAndLockedFalse(requestBody.getEmail());
        if(userEntity == null){
            throw new AppException("Không tìm thấy user");
        }

        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key){
                        return "DEFAULT";
                    }
                });

        String otpStr = generateOTP().toString();
        cache.put(requestBody.getEmail(), otpStr);

        EmailTemplate template = new EmailTemplate("email-otp.html");

        Map<String,String> replacements = new HashMap<>();
        String firstName = userEntity.getFirstName() == null ? "" : userEntity.getFirstName();
        String lastName = userEntity.getLastName() == null ? "" : userEntity.getLastName();

        replacements.put("user", firstName + " " + lastName);
        replacements.put("otpnum", otpStr);

        String message = template.getTemplate(replacements);

        emailService.sendOtpMessage(requestBody.getEmail(), "DATN - OTP", message);
    }

    @Override
    public ForgetPasswordOTPResponse forgetPasswordOTPRequest(ForgetPasswordOTPRequest requestBody) {
        if(requestBody.getEmail() == null || requestBody.getOtp() == null){
            throw new AppException("Không được null");
        }

        String otpStr = cache.getIfPresent(requestBody.getEmail());
        if(otpStr == null){
            throw new AppException("Mã OTP đã hết hiệu lực");
        }
        Integer otp = Integer.parseInt(otpStr);
        cache.invalidate(requestBody.getEmail());

        if(!otp.equals(requestBody.getOtp())){
            throw new AppException("Mã OTP không chính xác, yêu cầu thực hiện lại từ bước đầu");
        }

        cache = CacheBuilder.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key){
                        return "DEFAULT";
                    }
                });

        String uuid = UUID.randomUUID().toString();
        cache.put(requestBody.getEmail(), uuid);

        return new ForgetPasswordOTPResponse(uuid);
    }

    @Override
    public void forgetPasswordChangeRequest(ForgetPasswordChangeRequest requestBody) {
        if(requestBody.getEmail() == null || requestBody.getToken() == null || requestBody.getNewPassword() == null || requestBody.getNewPassword().isEmpty()){
            throw new AppException("Không được null hoặc rỗng");
        }

        String securityTokenStr = cache.getIfPresent(requestBody.getEmail());
        cache.invalidate(requestBody.getEmail());
        if(securityTokenStr == null || !securityTokenStr.equals(requestBody.getToken())){
            cache = null;
            throw new AppException("Không thành công");
        }

        cache = null;

        UserEntity userEntity = userRepository.findOneByEmailAndDeletedFalseAndLockedFalse(requestBody.getEmail());
        if(userEntity == null){
            throw new AppException("Không tìm thấy user");
        }

        userEntity.setPassword(passwordEncoder.encode(requestBody.getNewPassword()));
        userRepository.save(userEntity);
    }

    private UserEntity getCurrentUserEntity(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UserEntity userEntity = userRepository.findOneByIdAndDeletedFalseAndLockedFalse(userPrincipal.getId());
        if(userEntity == null){
            throw new AppException("Không tìm thấy user hiện tại");
        }
        return userEntity;
    }

    private Boolean checkAuthorities(List<String> compareAuthorities, UserPrincipal userPrincipal){
        for(String i : compareAuthorities){
            if(userPrincipal.getAuthorities().contains(new SimpleGrantedAuthority(i))){
                return true;
            }
        }
        return false;
    }

    private void checkDuplicateField(String email, String phone){

        if(email != null && userRepository.findByEmail(email) != null){
            throw new AppException("Email already exits");
        }

        if(phone != null && userRepository.findByPhone(phone) != null){
            throw new AppException("Phone number already exits");
        }
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

    private Integer generateOTP(){
        Random random = new Random();
        Integer number = 100000;
        return number + random.nextInt(900000);
    }
}
