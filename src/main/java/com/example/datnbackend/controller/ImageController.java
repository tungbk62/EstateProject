package com.example.datnbackend.controller;

import com.example.datnbackend.dto.MainResponse;
import com.example.datnbackend.dto.post.PostCreateRequest;
import com.example.datnbackend.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/image")
public class ImageController {
    @Autowired
    ImageService imageService;

    @PutMapping(value = "/avatar")
    ResponseEntity<Object> updateAvatarImage(@RequestPart("file") MultipartFile file){
        try{
            imageService.updateAvatarImage(file);
            return new ResponseEntity<>(new MainResponse(true, "Upload ảnh thành công"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping(value = "business/post/{id}")
    ResponseEntity<Object> uploadPostImage(@PathVariable Long id,
                                           @RequestPart("files") List<MultipartFile> files,
                                           @RequestParam(value = "main", required = false) Integer main){
        try{
            imageService.uploadPostImage(id, files, main);
            return new ResponseEntity<>(new MainResponse(true, "Upload ảnh thành công"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping(value = "business/post/{id}")
    ResponseEntity<Object> getPostImage(@PathVariable Long id){
        try{
            return ResponseEntity.ok(imageService.getPostImage(id));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping(value = "business/post/main/{id}")
    ResponseEntity<Object> changeMainImage(@PathVariable Long id){
        try{
            imageService.changeMainImage(id);
            return new ResponseEntity<>(new MainResponse(true, "Thay đổi thành công"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @DeleteMapping(value = "business/post")
    ResponseEntity<Object> deleteImage(@RequestBody List<Long> ids){
        try{
            imageService.deleteImage(ids);
            return new ResponseEntity<>(new MainResponse(true, "Xoá thành công"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

}
