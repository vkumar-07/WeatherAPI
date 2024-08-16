package com.example.WeatherAPI.service;

import com.example.WeatherAPI.dto.*;
import com.example.WeatherAPI.entity.Forecast;
import com.example.WeatherAPI.entity.Location;
import com.example.WeatherAPI.entity.UserProfile;
import com.example.WeatherAPI.integration.location.LocationAPIService;
import com.example.WeatherAPI.integration.weather.WeatherAPIService;
import com.example.WeatherAPI.integration.weather.domain.Current;
import com.example.WeatherAPI.integration.weather.domain.Day;
import com.example.WeatherAPI.integration.weather.domain.ForecastDay;
import com.example.WeatherAPI.integration.weather.domain.WeatherResponse;
import com.example.WeatherAPI.repository.ForecastDayRepository;
import com.example.WeatherAPI.repository.ForecastRepository;
import com.example.WeatherAPI.repository.LocationRepository;
import com.example.WeatherAPI.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class WeatherForecastServiceTest {

    @InjectMocks
    private WeatherForecastService weatherForecastService;

    @Mock
    private ForecastDayRepository forecastDayRepository;

    @Mock
    private ForecastRepository forecastRepository;

    @Mock
    private WeatherAPIService weatherAPIService;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private LocationAPIService locationAPIService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetOrCreateForecast_Found() {
        com.example.WeatherAPI.integration.weather.domain.Location location = com.example.WeatherAPI.integration.weather.domain.Location.builder().name("28.65,77.23").build();

        int day = 1;
        UserProfile user = UserProfile.builder().location(location.getName()).build();

        Forecast forecast = Forecast.builder().location(location.getName()).temp_c(20.0).temp_f(68.0).build();

        WeatherResponse weatherResponse = new WeatherResponse();
        ForecastResponse expectedResponse = ForecastResponse.builder().build();
        LocalDate targetDate = LocalDate.now().plusDays(day-1);

        Mockito.when(forecastRepository.findByLocation(location.getName())).thenReturn(Mono.just(forecast));
        Mockito.when(weatherAPIService.getForecast(location.getName(), day)).thenReturn(Mono.just(weatherResponse));
        Mockito.when(locationRepository.findByLocation(location.getName())).thenReturn(Mono.just(Location.builder().location(location.getName()).build()));
        Mockito.when(forecastDayRepository.findAllByForecastIdAndDateLessThanEqual(forecast.getId(),targetDate )).thenReturn(Flux.just(com.example.WeatherAPI.entity.ForecastDay.builder().build()));

        Flux<ForecastResponse> result = weatherForecastService.getOrCreateForecast(location.getName(), day, user);
        assertNotNull(result);
        assertEquals(1, result.count().block());
    }

    @Test
    void testGetOrCreateForecast_NotFound() {

        com.example.WeatherAPI.integration.weather.domain.Location location = com.example.WeatherAPI.integration.weather.domain.Location.builder().name("28.65,77.23").build();

        int day = 1;
        UserProfile user = UserProfile.builder().location(location.getName()).build();

        Forecast forecast = Forecast.builder().location(location.getName()).temp_c(20.0).temp_f(68.0).build();

        WeatherResponse weatherResponse = new WeatherResponse();
        weatherResponse.setLocation(location);
        weatherResponse.setCurrent(Current.builder().build());
        List<ForecastDay> forecastDays =new ArrayList<>();
        forecastDays.add(ForecastDay.builder().date("2024-07-26").day(Day.builder().build()).build());
        weatherResponse.setForecast(com.example.WeatherAPI.integration.weather.domain.Forecast.builder().forecastday(forecastDays).build());
        List<ForecastDayDTO> forecastDaysDTOs =new ArrayList<>();
        forecastDaysDTOs.add(ForecastDayDTO.builder().date("2024-07-26").day(DayDTO.builder().build()).build());
        ForecastResponse expectedResponse = ForecastResponse.builder()
                .location(location)
                .current(CurrentDTO.builder().build())
                .forecast(ForecastDTO.builder().forecastday(forecastDaysDTOs).build())
                .build();

        Mockito.when(forecastRepository.findByLocation(location.getName())).thenReturn(Mono.empty());
        Mockito.when(weatherAPIService.getForecast(location.getName(), day)).thenReturn(Mono.just(weatherResponse));
        Mockito.when(locationRepository.save(any())).thenReturn(Mono.just(Location.builder().build()));
        Mockito.when(userProfileRepository.save(any())).thenReturn(Mono.just(UserProfile.builder().build()));
        Mockito.when(forecastRepository.save(any())).thenReturn(Mono.just(forecast));
        List<com.example.WeatherAPI.entity.ForecastDay> forecastDayList =new ArrayList<>();
        Mockito.when(forecastDayRepository.saveAll(anyIterable())).thenReturn(Flux.fromIterable(forecastDayList));


        Flux<ForecastResponse> result = weatherForecastService.getOrCreateForecast(location.getName(), day, user);

        assertNotNull(result);
        assertEquals(1, result.count().block());
        assertEquals(expectedResponse, result.blockFirst());
    }

    @Test
    void testDeleteForecastDayById() {
        List<Long> forecastDayIds = Arrays.asList(1L, 2L, 3L);

        Mockito.when(forecastDayRepository.deleteById(anyLong())).thenReturn(Mono.empty());

        Mono<Void> result = weatherForecastService.deleteForecastDayById(forecastDayIds);

        assertNotNull(result);
        result.subscribe();

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(forecastDayRepository, times(forecastDayIds.size())).deleteById(captor.capture());

        List<Long> capturedIds = captor.getAllValues();
        assertEquals(forecastDayIds, capturedIds);
    }

}
