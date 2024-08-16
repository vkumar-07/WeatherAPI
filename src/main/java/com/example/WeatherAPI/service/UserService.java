package com.example.WeatherAPI.service;

import com.example.WeatherAPI.entity.UserProfile;
import com.example.WeatherAPI.repository.UserProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class UserService {

    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserProfileRepository userProfileRepository, PasswordEncoder passwordEncoder) {
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<UserProfile> saveUser(UserProfile userProfile) {
        userProfile.setPass(passwordEncoder.encode(userProfile.getPass()));
        return userProfileRepository.save(userProfile)
                .doOnError(error -> {
                    log.error("Error saving user: {}", error.getMessage());
                })
                .onErrorResume(error -> {
                    return Mono.error(new RuntimeException("Failed to save user due to an internal error"));
                });
    }
}
