package com.example.livebettingapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Odds {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double homeWin;
    private double draw;
    private double awayWin;
    private LocalDateTime updatedAt;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "match_id")
    @ToString.Exclude
    private Match match;

    @OneToMany(mappedBy = "odds", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OddsHistory> oddsHistories;
}
