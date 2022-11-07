package com.example.datnbackend.controller;

import com.example.datnbackend.dto.MainResponse;
import com.example.datnbackend.dto.post.PostCreateRequest;
import com.example.datnbackend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/post")
public class PostController {
    @Autowired
    PostService postService;

    @PostMapping
    ResponseEntity<Object> createPost(@RequestBody PostCreateRequest requestBody){
        try{
            return ResponseEntity.ok(postService.createPost(requestBody));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping(value = "/{id}")
    ResponseEntity<Object> updatePost(@RequestBody PostCreateRequest requestBody,
                                      @PathVariable Long id){
        try{
            return ResponseEntity.ok(postService.updatePost(id, requestBody));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping(value = "/public/list")
    ResponseEntity<Object> getPostDescriptionList(@RequestParam Integer page,
                                                  @RequestParam Integer size,
                                                  @RequestParam(required = false) String order,
                                                  @RequestParam(required = false) Long province,
                                                  @RequestParam(required = false) Long district,
                                                  @RequestParam(required = false) Long wards,
                                                  @RequestParam(required = false) String address,
                                                  @RequestParam(required = false) List<Long> type,
                                                  @RequestParam(required = false) Integer room,
                                                  @RequestParam(required = false) Double pricemin,
                                                  @RequestParam(required = false) Double pricemax){
        try{
            return ResponseEntity.ok(postService.getPostDescriptionList(page, size, order, province, district, wards,
                                                                        address, type, room, pricemin, pricemax));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping(value = "/public/{id}")
    ResponseEntity<Object> getPostDetail(@PathVariable Long id){
        try{
            return ResponseEntity.ok(postService.getPostDetail(id));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping(value = "/hide/{id}")
    @Secured("ROLE_BUSINESS")
    ResponseEntity<Object> hidePost(@PathVariable Long id,
                                    @RequestParam(value = "hide") Boolean hide){
        try{
            postService.hidePost(id, hide);
            return new ResponseEntity<>(new MainResponse(true, "Thay đổi trạng thái bài đăng thành công"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @DeleteMapping
    @Secured("ROLE_BUSINESS")
    ResponseEntity<Object> deleteMultiplePost(@RequestBody List<Long> ids){
        try{
            postService.deleteMultiplePost(ids);
            return new ResponseEntity<>(new MainResponse(true, "Thay đổi trạng thái bài đăng thành công"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping(value = "/admin/{id}")
    @Secured("ROLE_ADMIN")
    ResponseEntity<Object> changeStateOfHideLockedVerified(@PathVariable(value = "id") Long id,
                                                           @RequestParam(value = "hide", required = false) Boolean hide,
                                                           @RequestParam(value = "locked", required = false) Boolean locked,
                                                           @RequestParam(value = "verified", required = false) Boolean verified){
        try{
            postService.changeStateOfHideLockedVerified(id, hide, locked, verified);
            return new ResponseEntity<>(new MainResponse(true, "Thay đổi trạng thái bài đăng thành công"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

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
