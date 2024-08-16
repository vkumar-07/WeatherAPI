package com.example.WeatherAPI.repository;

import com.example.WeatherAPI.entity.Location;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface LocationRepository extends ReactiveCrudRepository<Location, Long> {
    @Query("SELECT * FROM location WHERE location = :location")
    Mono<Location> findByLocation(@Param("location") String location);
}
