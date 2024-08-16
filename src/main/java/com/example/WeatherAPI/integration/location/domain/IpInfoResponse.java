package com.example.WeatherAPI.integration.location.domain;

import lombok.Data;

@Data
public class IpInfoResponse {

    private String ip;
    private String city;
    private String region;
    private String country;
    private String loc;
    private String postal;
    private String timezone;

}
