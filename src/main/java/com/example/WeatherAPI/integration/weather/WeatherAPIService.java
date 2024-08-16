package com.example.WeatherAPI.integration.weather;

import com.example.WeatherAPI.integration.weather.domain.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WeatherAPIService {

    private final WebClient webClient;
    private final String path;
    private final String apiKey;

    @Autowired
    public WeatherAPIService(WebClient.Builder webClientBuilder,
                             @Value("${weatherapi.baseurl}") String baseUrl,
                             @Value("${weatherapi.path}") String path,
                             @Value("${weatherapi.apikey}") String apiKey) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.path = path;
        this.apiKey = apiKey;
    }

    public Mono<WeatherResponse> getForecast(String location, int day) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder.path(path)
                        .queryParam("key", apiKey)
                        .queryParam("q", location)
                        .queryParam("days", day)
                        .build())
                .retrieve()
                .bodyToMono(WeatherResponse.class);
    }

}
