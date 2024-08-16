package com.example.WeatherAPI.repository;

import com.example.WeatherAPI.entity.Forecast;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ForecastRepository extends ReactiveCrudRepository<Forecast, Long> {

    Mono<Forecast> findByLocation(String location);
}
