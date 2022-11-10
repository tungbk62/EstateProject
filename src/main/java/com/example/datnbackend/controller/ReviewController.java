package com.example.datnbackend.controller;

import com.example.datnbackend.dto.MainResponse;
import com.example.datnbackend.dto.post.PostCreateRequest;
import com.example.datnbackend.dto.review.ReviewCreateRequest;
import com.example.datnbackend.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/review")
public class ReviewController {
    @Autowired
    ReviewService reviewService;

    @PostMapping(value = "/customer/{id}")
    ResponseEntity<Object> createReview(@RequestBody ReviewCreateRequest requestBody,
                                        @RequestParam Long id){
        try{
            return ResponseEntity.ok(reviewService.createReview(id, requestBody));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping(value = "/customer/{id}")
    ResponseEntity<Object> updateReview(@RequestBody ReviewCreateRequest requestBody,
                                        @RequestParam Long id){
        try{
            return ResponseEntity.ok(reviewService.updateReview(id, requestBody));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping(value = "/public/{id}")
    ResponseEntity<Object> getReviewList(@RequestParam Integer page,
                                         @RequestParam Integer size,
                                         @RequestParam String order,
                                         @RequestParam Long id){
        try{
            return ResponseEntity.ok(reviewService.getReviewList(page, size, order, id));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @DeleteMapping(value = "/admin")
    @Secured("ROLE_ADMIN")
    ResponseEntity<Object> deleteReview(@RequestBody List<Long> ids){
        try{
            reviewService.deleteReview(ids);
            return new ResponseEntity<>(new MainResponse(true, "Xoá thành công"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }
}
