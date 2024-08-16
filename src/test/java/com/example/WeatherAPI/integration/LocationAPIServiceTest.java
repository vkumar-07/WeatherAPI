package com.example.WeatherAPI.integration;

import com.example.WeatherAPI.integration.location.LocationAPIService;
import com.example.WeatherAPI.integration.location.domain.IpInfoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class LocationAPIServiceTest {

    private WebClient.Builder webClientBuilder;
    private WebClient webClient;
    private LocationAPIService locationAPIService;

    @BeforeEach
    void setUp() {
        webClientBuilder = mock(WebClient.Builder.class);
        webClient = mock(WebClient.class);

        when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
        when(webClientBuilder.build()).thenReturn(webClient);

        locationAPIService = new LocationAPIService(webClientBuilder, "https://dummy.url", "dummyToken");

    }

    @Test
    void testGetLocation() {
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        UriBuilder uriBuilder = mock(UriBuilder.class);
        URI expectedUri = URI.create("http://example.com?token=testToken");

        when(uriBuilder.queryParam(eq("testToken"), anyString())).thenReturn(uriBuilder);
        when(uriBuilder.build()).thenReturn(expectedUri);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(argThat((Function<UriBuilder, URI> uriBuilderFunction) -> expectedUri.equals(uriBuilderFunction.apply(uriBuilder)))))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        IpInfoResponse mockResponse = new IpInfoResponse();
        mockResponse.setLoc("12.34.56.78");
        when(responseSpec.bodyToMono(IpInfoResponse.class)).thenReturn(Mono.just(mockResponse));

        Mono<String> locationMono = locationAPIService.getLocation();

        StepVerifier.create(locationMono)
                .expectNext("12.34.56.78")
                .verifyComplete();
    }


    @Test
    void testGetLocation_Exception() {
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(IpInfoResponse.class)).thenReturn(Mono.error(new WebClientResponseException(404, "Not Found", null, null, null)));

        Mono<String> locationMono = locationAPIService.getLocation();

        StepVerifier.create(locationMono)
                .expectError(WebClientResponseException.class)
                .verify();
    }
}
