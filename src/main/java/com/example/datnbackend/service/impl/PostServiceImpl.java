package com.example.datnbackend.service.impl;

import com.example.datnbackend.dto.exception.AppException;
import com.example.datnbackend.entity.PostEntity;
import com.example.datnbackend.entity.UserEntity;
import com.example.datnbackend.repository.PostRepository;
import com.example.datnbackend.repository.UserRepository;
import com.example.datnbackend.security.UserPrincipal;
import com.example.datnbackend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;

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
}
