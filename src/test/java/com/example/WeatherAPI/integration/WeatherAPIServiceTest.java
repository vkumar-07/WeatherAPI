package com.example.WeatherAPI.integration;

import com.example.WeatherAPI.integration.weather.WeatherAPIService;
import com.example.WeatherAPI.integration.weather.domain.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class WeatherAPIServiceTest {


    private WebClient.Builder webClientBuilder;
    private WebClient webClient;

    private WeatherAPIService weatherAPIService;

    @BeforeEach
    void setUp() {
        webClientBuilder = mock(WebClient.Builder.class);
        webClient = mock(WebClient.class);

        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);

        weatherAPIService = new WeatherAPIService(webClientBuilder, "https://dummy.url", "/path","dummyKey");
    }

    @Test
    void testGetForecast() {
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        WeatherResponse mockResponse = new WeatherResponse();
        when(responseSpec.bodyToMono(WeatherResponse.class)).thenReturn(Mono.just(mockResponse));

        Mono<WeatherResponse> weatherResponseMono = weatherAPIService.getForecast("London", 1);

        StepVerifier.create(weatherResponseMono)
                .expectNext(mockResponse)
                .verifyComplete();
    }

    @Test
    void testGetForecast_Exception() {
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(WeatherResponse.class)).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<WeatherResponse> weatherResponseMono = weatherAPIService.getForecast("London", 1);

        StepVerifier.create(weatherResponseMono)
                .expectError(WebClientResponseException.class)
                .verify();
    }

}
