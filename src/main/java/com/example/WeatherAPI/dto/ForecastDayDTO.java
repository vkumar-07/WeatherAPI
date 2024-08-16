package com.example.WeatherAPI.dto;


import com.example.WeatherAPI.integration.weather.domain.Hour;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ForecastDayDTO {
    private Long id;
    private String date;
    private long date_epoch;
    private DayDTO day;
    private List<Hour> hour;

}
