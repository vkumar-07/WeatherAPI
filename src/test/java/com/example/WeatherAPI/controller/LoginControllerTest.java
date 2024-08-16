package com.example.WeatherAPI.controller;

import com.example.WeatherAPI.dto.LoginRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

@SpringBootTest
@AutoConfigureWebTestClient
public class LoginControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ReactiveAuthenticationManager authenticationManager;

    @MockBean
    private ServerSecurityContextRepository securityContextRepository;


    @Test
    public void testLogin_Success() {
        LoginRequest loginRequest = LoginRequest.builder().username("user1").password("password").build();

        Authentication authToken = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        Authentication authentication = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContext context = new SecurityContextImpl(authentication);

        Mockito.when(authenticationManager.authenticate(authToken)).thenReturn(Mono.just(authentication));
        Mockito.when(securityContextRepository.save(Mockito.any(), Mockito.any())).thenReturn(Mono.empty());

        webTestClient.post().uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void testLogin_InvalidCredentials() {
        LoginRequest loginRequest = LoginRequest.builder().username("user1").password("password").build();

        Authentication authToken = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

        Mockito.when(authenticationManager.authenticate(authToken)).thenReturn(Mono.error(new BadCredentialsException("Invalid credentials")));

        webTestClient.post().uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void testLogin_ErrorSavingSecurityContext() {
        LoginRequest loginRequest = LoginRequest.builder().username("user1").password("password").build();

        Authentication authToken = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        Authentication authentication = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContext context = new SecurityContextImpl(authentication);

        Mockito.when(authenticationManager.authenticate(authToken)).thenReturn(Mono.just(authentication));
        Mockito.when(securityContextRepository.save(Mockito.any(), Mockito.any())).thenReturn(Mono.error(new RuntimeException("Error saving security context")));

        webTestClient.post().uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
