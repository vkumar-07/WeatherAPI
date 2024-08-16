package com.example.WeatherAPI.integration.weather.domain;

import lombok.Data;

@Data
public class Alert {
    private String headline;
    private String msgtype;
    private String severity;
    private String urgency;
    private String areas;
    private String category;
    private String certainty;
    private String event;
    private String note;
    private String effective;
    private String expires;
    private String desc;
    private String instruction;
}
