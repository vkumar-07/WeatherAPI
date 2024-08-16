package com.example.WeatherAPI.repository;

import com.example.WeatherAPI.entity.ForecastDay;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
@Repository
public interface ForecastDayRepository extends ReactiveCrudRepository<ForecastDay, Long> {

    Flux<ForecastDay> findAll();

    Flux<ForecastDay> findAllByForecastIdAndDate(Long forecastId, LocalDate targetDate);

    Flux<ForecastDay> findAllByForecastIdAndDateLessThanEqual(Long forecastId,  LocalDate targetDate);

}
