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

@RestController
@RequestMapping(value = "/image")
public class ImageController {
    @Autowired
    ImageService imageService;

    @PostMapping(value = "/avatar/{id}")
    ResponseEntity<Object> uploadAvatarImage(@PathVariable Long id,
                                             @RequestParam("file") MultipartFile file){
        try{
            imageService.updateAvatarImage(id, file);
            return new ResponseEntity<>(new MainResponse(true, "Upload ảnh thành công"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

}
