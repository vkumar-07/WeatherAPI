package com.example.WeatherAPI.controller;

import com.example.WeatherAPI.entity.UserProfile;
import com.example.WeatherAPI.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureWebTestClient
public class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    @Test
    public void testRegisterUser_Success() {
        UserProfile userProfile = UserProfile.builder().username("testuser").pass("password").location("28.65,77.23").build();

        Mockito.when(userService.saveUser(Mockito.any(UserProfile.class)))
                .thenReturn(Mono.just(userProfile));

        webTestClient.post().uri("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userProfile)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserProfile.class)
                .value(user -> {
                    assertEquals("testuser", user.getUsername());
                    assertEquals("28.65,77.23", user.getLocation());
                });
    }

    @Test
    public void testRegisterUser_InternalServerError() {
        UserProfile userProfile = UserProfile.builder().username("testuser").pass("password").location("28.65,77.23").build();

        Mockito.when(userService.saveUser(Mockito.any(UserProfile.class)))
                .thenReturn(Mono.error(new RuntimeException("Error saving user")));

        webTestClient.post().uri("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userProfile)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
