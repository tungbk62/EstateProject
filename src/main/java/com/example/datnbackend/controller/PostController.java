package com.example.datnbackend.controller;

import com.example.datnbackend.dto.MainResponse;
import com.example.datnbackend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/post")
public class PostController {
    @Autowired
    PostService postService;

    @PostMapping(value = "/save/{id}")
    ResponseEntity<Object> savePostToUser(@PathVariable Long id){
        try{
            postService.savePostToUser(id);
            return new ResponseEntity<>(new MainResponse(true, "Lưu tin thành công"), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }
}
