package com.example.WeatherAPI.controller;

import com.example.WeatherAPI.dto.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/auth")
public class LoginController {

    private final ReactiveAuthenticationManager authenticationManager;
    private final ServerSecurityContextRepository securityContextRepository;

    @Autowired
    public LoginController(ReactiveAuthenticationManager authenticationManager,
                           ServerSecurityContextRepository securityContextRepository) {
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
    }

    @PostMapping("/login")
    public Mono<?> login(@RequestBody LoginRequest authRequest, ServerWebExchange exchange) {

        Authentication authToken = new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword());

        return this.authenticationManager.authenticate(authToken)
                .flatMap(authentication -> {
                    log.info("User authenticated: {}", authRequest.getUsername());
                    SecurityContext context = new SecurityContextImpl(authentication);
                    return this.securityContextRepository.save(exchange, context)
                            .doOnSuccess(aVoid -> log.info("Security context saved successfully for user: {}", authRequest.getUsername()))
                            .doOnError(error -> log.error("Error saving security context for user {}: {}", authRequest.getUsername(), error.getMessage()));
                })
                .doOnSuccess(aVoid -> exchange.getResponse().setStatusCode(HttpStatus.OK))
                .doOnError(error -> {
                    log.error("Authentication error: {}", error.getMessage());
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                })
                .then();
    }
}