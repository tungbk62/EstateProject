package com.example.datnbackend.controller;

import com.example.datnbackend.dto.MainResponse;
import com.example.datnbackend.dto.contact.ContactCreateRequest;
import com.example.datnbackend.dto.review.ReviewCreateRequest;
import com.example.datnbackend.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/contact")
public class ContactRequestController {
    @Autowired
    ContactService contactService;

    @PostMapping(value = "/public/{id}")
    ResponseEntity<Object> createContact(@RequestBody ContactCreateRequest requestBody,
                                         @PathVariable Long id){
        try{
            return ResponseEntity.ok(contactService.createContact(id, requestBody));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping(value = "/business")
    @Secured("ROLE_BUSINESS")
    ResponseEntity<Object> getContactList(@RequestParam Integer page,
                                          @RequestParam Integer size,
                                          @RequestParam(required = false) String order,
                                          @RequestParam(required = false) Boolean viewed,
                                          @RequestParam(required = false) Boolean handled){
        try{
            return ResponseEntity.ok(contactService.getContactList(page, size, order, viewed, handled));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping(value = "/business/{id}")
    @Secured("ROLE_BUSINESS")
    ResponseEntity<Object> getContactDetail(@PathVariable Long id){
        try{
            return ResponseEntity.ok(contactService.getContactDetail(id));
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping(value = "/business/{id}")
    @Secured("ROLE_BUSINESS")
    ResponseEntity<Object> changeStateHandled(@PathVariable Long id,
                                              @RequestParam Boolean handled){
        try{
            contactService.changeStateHandled(id, handled);
            return new ResponseEntity<>(new MainResponse(true, "Thay đổi thành công"), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(new MainResponse(false, e.getMessage()), HttpStatus.EXPECTATION_FAILED);
        }
    }
}
