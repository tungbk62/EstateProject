package com.example.datnbackend.service;

public interface CacheService {
    void put (String key, String value);
    String getIfPresent(String key);
    void invalidKey(String key);
}
