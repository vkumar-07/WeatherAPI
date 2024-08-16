package com.example.WeatherAPI.repository;

import com.example.WeatherAPI.entity.UserProfile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserProfileRepository extends ReactiveCrudRepository<UserProfile, Long> {
    Mono<UserProfile> findByUsername(String username);
}