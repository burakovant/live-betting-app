package com.example.livebettingapp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String league;
    private String homeTeam;
    private String awayTeam;
    private LocalDateTime startTime;
    private Integer betCount;

    @OneToOne(mappedBy = "match", cascade = CascadeType.ALL)
    private Odds odds;
}
