package com.example.WeatherAPI.service;

import com.example.WeatherAPI.entity.UserProfile;
import com.example.WeatherAPI.repository.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    public void testSaveUser_Success() {
        UserProfile userProfile = UserProfile.builder().username("testuser").pass("password").build();
        UserProfile savedUserProfile = UserProfile.builder().username("testuser").pass("encodedPassword").build();

        Mockito.when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        Mockito.when(userProfileRepository.save(any(UserProfile.class))).thenReturn(Mono.just(savedUserProfile));

        Mono<UserProfile> result = userService.saveUser(userProfile);

        StepVerifier.create(result)
                .expectNextMatches(user ->
                        user.getUsername().equals("testuser") &&
                                user.getPass().equals("encodedPassword"))
                .verifyComplete();

        Mockito.verify(passwordEncoder).encode("password");
        Mockito.verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    public void testSaveUser_Error() {
        UserProfile userProfile = UserProfile.builder().username("testuser").pass("password").build();

        Mockito.when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        Mockito.when(userProfileRepository.save(any(UserProfile.class))).thenReturn(Mono.error(new RuntimeException("Error saving user")));

        Mono<UserProfile> result = userService.saveUser(userProfile);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException && throwable.getMessage().equals("Failed to save user due to an internal error"))
                .verify();

        Mockito.verify(passwordEncoder).encode("password");
        Mockito.verify(userProfileRepository).save(any(UserProfile.class));
    }
}
