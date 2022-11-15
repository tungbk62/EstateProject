package com.example.datnbackend.service.impl;

import com.example.datnbackend.service.CacheService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
public class CacheServiceImpl implements CacheService {
    private LoadingCache<String, String> cache;

    public CacheServiceImpl(){
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key){
                        return "DEFAULT";
                    }
                });
    }

    @Override
    public void put(String key, String value) {
        cache.put(key, value);
    }

    @Override
    public String getIfPresent(String key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void invalidKey(String key) {
        cache.invalidate(key);
    }
}
