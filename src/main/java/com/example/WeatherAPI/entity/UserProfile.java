package com.example.WeatherAPI.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "userProfile")
public class UserProfile {
    @Id
    private Long id;
    private String username;
    private String pass;
    private String location;

}
