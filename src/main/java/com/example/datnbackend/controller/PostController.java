package com.example.datnbackend.controller;

import com.example.datnbackend.dto.MainResponse;
import com.example.datnbackend.dto.post.PostCreateRequest;
import com.example.datnbackend.dto.post.PostReportCreateRequest;
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

    @PostMapping(value = "/business")
    @Secured("ROLE_BUSINESS")
    ResponseEntity<Object> createPost(@RequestBody PostCreateRequest requestBody){
        try{
            return ResponseEntity.ok(postService.createPost(requestBody));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping(value = "/business/{id}")
    @Secured("ROLE_BUSINESS")
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
                                                  @RequestParam(required = false) List<Long> type,
                                                  @RequestParam(required = false) Integer room,
                                                  @RequestParam(required = false) Double pricemin,
                                                  @RequestParam(required = false) Double pricemax,
                                                  @RequestParam(required = false) Double areamin,
                                                  @RequestParam(required = false) Double areamax){
        try{
            return ResponseEntity.ok(postService.getPostDescriptionList(page, size, order, province, district, wards,
                    type, room, pricemin, pricemax, areamin, areamax));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping(value = "/public/list/search")
    ResponseEntity<Object> getPostDescriptionListSearch(@RequestParam Integer page,
                                                        @RequestParam Integer size,
                                                        @RequestParam(required = false) String order,
                                                        @RequestParam(required = false) String search){
        try{
            return ResponseEntity.ok(postService.getPostDescriptionListSearch(page, size, order, search));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping(value = "/business/list")
    @Secured("ROLE_BUSINESS")
    ResponseEntity<Object> getPostDescriptionListForBusiness(@RequestParam Integer page,
                                                             @RequestParam Integer size) {
        try {
            return ResponseEntity.ok(postService.getPostDescriptionListForBusiness(page, size));
        } catch (Exception e) {
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping(value = "/admin/list")
    @Secured("ROLE_ADMIN")
    ResponseEntity<Object> getPostDescriptionListForAdmin(@RequestParam Integer page,
                                                          @RequestParam Integer size,
                                                          @RequestParam(value = "userId", required = false) Long userId) {
        try {
            return ResponseEntity.ok(postService.getPostDescriptionListForAdmin(page, size, userId));
        } catch (Exception e) {
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

    @GetMapping(value = "/admin/{id}")
    @Secured("ROLE_ADMIN")
    ResponseEntity<Object> getPostDetailForAdmin(@PathVariable Long id){
        try{
            return ResponseEntity.ok(postService.getPostDetailForAdmin(id));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping(value = "/business/{id}")
    @Secured("ROLE_BUSINESS")
    ResponseEntity<Object> getPostDetailForBusiness(@PathVariable Long id){
        try{
            return ResponseEntity.ok(postService.getPostDetailForBusiness(id));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping(value = "/business/hide/{id}")
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

    @DeleteMapping(value = "/business")
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

    @GetMapping(value = "/save/list")
    ResponseEntity<Object> getPostSaveList(@RequestParam Integer page,
                                           @RequestParam Integer size){
        try{
            return ResponseEntity.ok(postService.getDescriptionPostListSave(page, size));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @DeleteMapping(value = "/save")
    ResponseEntity<Object> deletePostSave(@RequestBody List<Long> ids){
        try{
            postService.deletePostSave(ids);
            return new ResponseEntity<>(new MainResponse(true, "Xoá thành công"), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping(value = "/public/report/{id}")
    ResponseEntity<Object> createPostReport(@PathVariable Long id,
                                            @RequestBody PostReportCreateRequest requestBody){
        try{
            return ResponseEntity.ok(postService.createPostReport(id, requestBody));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping(value = "/admin/report")
    @Secured("ROLE_ADMIN")
    ResponseEntity<Object> getPostReportList(@RequestParam(value = "page") Integer page,
                                             @RequestParam(value = "size") Integer size,
                                             @RequestParam(value = "order", required = false) String order,
                                             @RequestParam(value = "postId", required = false) Long postId,
                                             @RequestParam(value = "typeId", required = false) Long typeId,
                                             @RequestParam(value = "userId", required = false) Long userId,
                                             @RequestParam(value = "handled", required = false) Boolean handled){
        try{
            return ResponseEntity.ok(postService.getPostReportList(page, size, order, postId, typeId, userId, handled));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping(value = "/admin/report/{id}")
    @Secured("ROLE_ADMIN")
    ResponseEntity<Object> getPostReportDetail(@PathVariable Long id){
        try{
            return ResponseEntity.ok(postService.getPostReportDetail(id));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping(value = "/admin/report/{id}")
    @Secured("ROLE_ADMIN")
    ResponseEntity<Object> changeHandledState(@PathVariable Long id,
                                              @RequestParam("handled") Boolean handled){
        try{
            postService.changeHandledState(id, handled);
            return new ResponseEntity<>(new MainResponse(true, "Thay đổi trạng thái thành công"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @DeleteMapping(value = "/admin/report")
    @Secured("ROLE_ADMIN")
    ResponseEntity<Object> deletePostReport(@RequestBody List<Long> ids){
        try{
            postService.deletePostReport(ids);
            return new ResponseEntity<>(new MainResponse(true, "Xoá thành công"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }
}
