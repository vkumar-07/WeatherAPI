package com.example.WeatherAPI.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DayDTO {
    private Long id;
    private double maxtemp_c;
    private double maxtemp_f;
    private double mintemp_c;
    private double mintemp_f;
    private double avgtemp_c;
    private double avgtemp_f;
    private double maxwind_mph;
    private double maxwind_kph;
    private double totalprecip_mm;
    private double totalprecip_in;
    private double avgvis_km;
    private double avgvis_miles;
    private int avghumidity;
    private int daily_will_it_rain;
    private int daily_chance_of_rain;
    private int daily_will_it_snow;
    private int daily_chance_of_snow;
    private double uv;
}
