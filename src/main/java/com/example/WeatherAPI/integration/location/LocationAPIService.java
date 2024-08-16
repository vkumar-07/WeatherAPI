package com.example.WeatherAPI.integration.location;

import com.example.WeatherAPI.integration.location.domain.IpInfoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
public class LocationAPIService {

    private final WebClient webClient;
    private final String token;

    @Autowired
    public LocationAPIService(WebClient.Builder webClientBuilder,
                              @Value("${ipinfo.baseurl}") String baseUrl,
                              @Value("${ipinfo.token}") String token) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.token = token;
    }

    public Mono<String> getLocation() {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("token", token)
                        .build())
                .retrieve()
                .bodyToMono(IpInfoResponse.class)
                .map(IpInfoResponse::getLoc);
    }
}
