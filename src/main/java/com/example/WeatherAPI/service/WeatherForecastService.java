package com.example.WeatherAPI.service;

import com.example.WeatherAPI.dto.*;
import com.example.WeatherAPI.entity.Forecast;
import com.example.WeatherAPI.entity.Location;
import com.example.WeatherAPI.entity.ForecastDay;
import com.example.WeatherAPI.entity.UserProfile;
import com.example.WeatherAPI.integration.location.LocationAPIService;
import com.example.WeatherAPI.integration.weather.WeatherAPIService;
import com.example.WeatherAPI.integration.weather.domain.Current;
import com.example.WeatherAPI.integration.weather.domain.WeatherResponse;
import com.example.WeatherAPI.repository.ForecastDayRepository;
import com.example.WeatherAPI.repository.ForecastRepository;
import com.example.WeatherAPI.repository.LocationRepository;
import com.example.WeatherAPI.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherForecastService {

    @Autowired
    private ForecastDayRepository forecastDayRepository;

    @Autowired
    private ForecastRepository forecastRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private WeatherAPIService weatherAPIService;

    @Autowired
    private LocationAPIService locationAPIService;
    @Autowired
    private UserProfileRepository userProfileRepository;



    public Flux<ForecastResponse> getOrCreateForecast(String location, int day, UserProfile user) {
        return forecastRepository.findByLocation(location)
                .flatMapMany(forecast -> {
                    System.out.println("flatMapMany block executed");
                    return mapForecastToResponse(forecast, day).flux();
                })
                .switchIfEmpty(
                        weatherAPIService.getForecast(location, day)
                                .flatMapMany(weatherResponse -> {
                                    ForecastResponse forecastResponse = mapToForecast(weatherResponse, user);
                                    List<ForecastDay> forecastDays = this.mapToForecastDays(weatherResponse.getForecast().getForecastday());
                                    return forecastRepository.save(mapCurrentToForecast(forecastResponse))
                                            .flatMapMany(savedForecast -> {
                                                forecastDays.forEach(forecastDay -> forecastDay.setForecastId(savedForecast.getId()));
                                                return forecastDayRepository.saveAll(forecastDays)
                                                        .collectList()
                                                        .flatMapMany(savedDays -> Flux.just(forecastResponse));
                                            });
                                })
                );
    }



    public Mono<ForecastResponse> mapForecastToResponse(Forecast forecast, int day) {
        return locationRepository.findByLocation(forecast.getLocation())
                .flatMap(location -> {
                    String[] latLon = location.getLocation().split(",");
                    CurrentDTO current= CurrentDTO.builder().temp_c(forecast.getTemp_c()).temp_f(forecast.getTemp_f()).build();

                    return fetchedForecastDays(forecast.getId(), day)
                            .map(forecastDays -> ForecastResponse.builder()
                                    .current(current)
                                    .location(com.example.WeatherAPI.integration.weather.domain.Location.builder()
                                            .region(location.getRegion())
                                            .name(location.getName())
                                            .country(location.getCountry())
                                            .lat(Double.parseDouble(latLon[0].trim()))
                                            .lon(Double.parseDouble(latLon[1].trim()))
                                            .build())
                                    .forecast(ForecastDTO.builder()
                                            .forecastday(forecastDays)
                                            .build())
                                    .build()
                            );
                })
                .switchIfEmpty(Mono.defer(() -> {
                    return Mono.just(ForecastResponse.builder()
                            .current(CurrentDTO.builder().build())
                            .location(com.example.WeatherAPI.integration.weather.domain.Location.builder().build())
                            .build());
                }));
    }

    private Mono<List<ForecastDayDTO>> fetchedForecastDays(Long forecastId, int day) {
        LocalDate targetDate = LocalDate.now().plusDays(day-1);
        return forecastDayRepository.findAllByForecastIdAndDateLessThanEqual(forecastId,targetDate)
                .map(this::mapToForecastDayDto)
                .collectList();

    }

    private ForecastResponse mapToForecast(WeatherResponse weatherResponse, UserProfile user) {
        Location locationEntity = maptoLocation(weatherResponse.getLocation());
        locationRepository.save(locationEntity).subscribe();
        user.setLocation(weatherResponse.getLocation().getLat()+","+weatherResponse.getLocation().getLon());
        userProfileRepository.save(user).subscribe();

        return ForecastResponse.builder()
                .location(weatherResponse.getLocation())
                .current(mapperCurrentToDTO(weatherResponse.getCurrent()))
                .forecast(mapperForcastToDTO(weatherResponse.getForecast()))
                .build();
    }

    private ForecastDTO mapperForcastToDTO(com.example.WeatherAPI.integration.weather.domain.Forecast forecast) {
        List<ForecastDayDTO> forecastDayList = new ArrayList<>();
        forecast.getForecastday().forEach(day -> {
            ForecastDayDTO forecastDayDTO = ForecastDayDTO.builder()
                    .date(day.getDate())
                    .day(DayDTO.builder()
                            .maxtemp_c(day.getDay().getMaxtemp_c())
                            .maxtemp_f(day.getDay().getMaxtemp_f())
                            .mintemp_c(day.getDay().getMintemp_c())
                            .mintemp_f(day.getDay().getMintemp_f())
                            .build()).build();
            forecastDayList.add(forecastDayDTO);
        });
        return ForecastDTO.builder().forecastday(forecastDayList).build();

    }

    private CurrentDTO mapperCurrentToDTO(Current current) {
        return CurrentDTO.builder()
                .cloud(current.getCloud())
                .uv(current.getUv())
                .temp_f(current.getTemp_f())
                .temp_c(current.getTemp_c())
                .humidity(current.getHumidity())
                .is_day(current.getIs_day())
                .build();
    }


    private Location maptoLocation(com.example.WeatherAPI.integration.weather.domain.Location location) {
       return Location.builder()
               .region(location.getRegion())
               .name(location.getName())
               .location(location.getLat()+","+location.getLon())
               .country(location.getCountry()).build();

    }

    private Forecast mapCurrentToForecast(ForecastResponse forecastResponse) {
        return Forecast.builder()
                .location(forecastResponse.getLocation().getLat()+","+forecastResponse.getLocation().getLon())
                .temp_c(forecastResponse.getCurrent().getTemp_c())
                .temp_f(forecastResponse.getCurrent().getTemp_f())
                .is_day(forecastResponse.getCurrent().getIs_day())
                .humidity(forecastResponse.getCurrent().getHumidity()).build();
    }

    public List<ForecastDay> mapToForecastDays(List<com.example.WeatherAPI.integration.weather.domain.ForecastDay> forecastdayDtos) {
        List<ForecastDay> forecastDayList = new ArrayList<>();
        forecastdayDtos.forEach(dto -> {
            ForecastDay entity = ForecastDay.builder()
                    .date(LocalDate.parse(dto.getDate()))
                    .maxTempC(dto.getDay().getMaxtemp_c())
                    .maxTempF(dto.getDay().getMaxtemp_f())
                    .minTempC(dto.getDay().getMintemp_c())
                    .minTempF(dto.getDay().getMintemp_f()).build();
            forecastDayList.add(entity);
        });
        return forecastDayList;
    }

    public ForecastDayDTO mapToForecastDayDto(ForecastDay forecastday) {
        ForecastDayDTO forecastDay = ForecastDayDTO.builder()
            .date(String.valueOf(forecastday.getDate()))
                .id(forecastday.getId())
                .day(DayDTO.builder()
                    .maxtemp_c(forecastday.getMaxTempC())
                    .maxtemp_f(forecastday.getMaxTempF())
                    .mintemp_c(forecastday.getMinTempC())
                    .mintemp_f(forecastday.getMinTempF())
                    .build())
                .build();
        return forecastDay;
    }


    public Flux<ForecastResponse> getOrCreateForecastForUserAsFlux(int day, UserProfile user) {
        return locationAPIService.getLocation()
                .flatMapMany(location -> this.getOrCreateForecast(location, day,user));
    }

    public Mono<Void> deleteForecastDayById(List<Long> forecastDayIds) {
        return Flux.fromIterable(forecastDayIds)
                .flatMap(forecastDayId -> forecastDayRepository.deleteById(forecastDayId))
                .then();
    }
}
