package com.example.WeatherAPI.controller;

import com.example.WeatherAPI.dto.ForecastResponse;
import com.example.WeatherAPI.repository.UserProfileRepository;
import com.example.WeatherAPI.service.WeatherForecastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@RestController
@RequestMapping("/api")
public class WeatherForecastController {

    @Autowired
    WeatherForecastService weatherForecastService;

    @Autowired
    UserProfileRepository userProfileRepository;

    @GetMapping("/forecast")
    public Flux<ForecastResponse> getForecast(@RequestParam int day, Authentication authentication) {
        String username = authentication.getName();
        return userProfileRepository.findByUsername(username)
                .flatMapMany(user -> {
                    if (user.getLocation() != null) {
                        return weatherForecastService.getOrCreateForecast(user.getLocation(),day, user);
                    } else {
                        return weatherForecastService.getOrCreateForecastForUserAsFlux(day,user);
                    }
                });
    }


    @DeleteMapping("/forecastDay")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteTForecastDay(@RequestBody List<Long> forecastDayIds) {
        return weatherForecastService.deleteForecastDayById(forecastDayIds);
    }


}
