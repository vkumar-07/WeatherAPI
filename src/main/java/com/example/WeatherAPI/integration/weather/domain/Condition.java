package com.example.WeatherAPI.integration.weather.domain;

import lombok.Data;

@Data
public class Condition {
    private String text;
    private String icon;
    private int code;
}
