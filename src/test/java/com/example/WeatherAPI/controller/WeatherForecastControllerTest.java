package com.example.WeatherAPI.controller;

import com.example.WeatherAPI.dto.ForecastResponse;
import com.example.WeatherAPI.entity.UserProfile;
import com.example.WeatherAPI.repository.UserProfileRepository;
import com.example.WeatherAPI.service.WeatherForecastService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;


@SpringBootTest
@WithMockUser(username = "user1", roles = {"USER"})
@AutoConfigureWebTestClient
public class WeatherForecastControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserProfileRepository userProfileRepository;

    @MockBean
    private WeatherForecastService weatherForecastService;


    @Test
    void testGetForecastWithLocation() throws Exception {
        UserProfile user = UserProfile.builder().username("user1").location("someLocation").build();

        ForecastResponse forecastResponse = ForecastResponse.builder().build();

        Mockito.when(userProfileRepository.findByUsername("user1")).thenReturn(Mono.just(user));
        Mockito.when(weatherForecastService.getOrCreateForecast("someLocation", 1, user))
                        .thenReturn(Flux.just(forecastResponse));

        webTestClient.get().uri("/api/forecast?day=1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ForecastResponse.class)
                .hasSize(1)
                .contains(forecastResponse);

    }

    @Test
    void testGetForecastWithoutLocation() {

        UserProfile user = UserProfile.builder().username("user1").build();

        ForecastResponse forecastResponse = ForecastResponse.builder().build();

        Mockito.when(userProfileRepository.findByUsername("user1")).thenReturn(Mono.just(user));
        Mockito.when(weatherForecastService.getOrCreateForecastForUserAsFlux(1, user))
                .thenReturn(Flux.just(forecastResponse));

        webTestClient.get().uri("/api/forecast?day=1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ForecastResponse.class)
                .hasSize(1)
                .contains(forecastResponse);
    }


    @Test
    void testDeleteForecastDays() {
        List<Long> forecastDayIds = Arrays.asList(1L, 2L, 3L);

        Mockito.when(weatherForecastService.deleteForecastDayById(forecastDayIds)).thenReturn(Mono.empty());

        webTestClient.method(HttpMethod.DELETE)
                .uri("/api/forecastDay")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(forecastDayIds)
                .exchange()
                .expectStatus().isNoContent();

    }
}

