package com.example.WeatherAPI.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@Table("forecastDay")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForecastDay {

    @Id
    private Long id;

    @Column("forecast_id")
    private Long forecastId;

    @Column("date")
    private LocalDate date;

    @Column("maxtemp_c")
    private double maxTempC;

    @Column("mintemp_c")
    private double minTempC;

    @Column("maxtemp_f")
    private double maxTempF;

    @Column("mintemp_f")
    private double minTempF;

    private long date_epoch;

}
