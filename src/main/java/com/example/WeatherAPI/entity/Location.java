package com.example.WeatherAPI.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class Location {

    @Id
    private Long id;
    private String location;
    private String name;
    private String region;
    private String country;

}
