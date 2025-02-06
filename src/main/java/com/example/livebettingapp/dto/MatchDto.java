package com.example.livebettingapp.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatchDto {
    private Long id;
    private String league;
    private String homeTeam;
    private String awayTeam;
    private LocalDateTime startTime;
    private Integer betCount;
    private Long oddsId;
}
