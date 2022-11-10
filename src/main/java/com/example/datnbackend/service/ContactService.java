package com.example.datnbackend.service;

import com.example.datnbackend.dto.contact.ContactCreateRequest;
import com.example.datnbackend.dto.contact.ContactDescriptionResponse;
import com.example.datnbackend.dto.contact.ContactDetailResponse;

import java.util.List;

public interface ContactService {
    ContactDetailResponse createContact(Long id, ContactCreateRequest requestBody);
    List<ContactDescriptionResponse> getContactList(Integer page, Integer size, String order, Boolean viewed, Boolean handled);
    ContactDetailResponse getContactDetail(Long id);
    void changeStateHandled(Long id, Boolean handled);
    void deleteContactRequest(List<Long> ids);
}
