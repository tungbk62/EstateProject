package com.example.datnbackend.security;

import com.example.datnbackend.entity.UserEntity;
import com.example.datnbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger logger = Logger.getLogger("CustomUserDetailsService");

    @Autowired(required = true)
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        logger.info("loadUserByUsername");
        UserEntity user = userRepository.findByEmailWithDeletedFalse(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found with username or email : " + username)
        );

        return UserPrincipal.create(user);
    }


    @Transactional
    public UserDetails loadUserById(Long id) {
        logger.info("loadUserById");
        UserEntity user = userRepository.findByIdWithDeletedFalseAndLockedFalse(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found with id : " + id)
        );

        return UserPrincipal.create(user);
    }
}

