package com.example.livebettingapp.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OddsHistoryDto {
    private Long id;
    private double homeWin;
    private double draw;
    private double awayWin;
    private LocalDateTime updatedAt;
}
