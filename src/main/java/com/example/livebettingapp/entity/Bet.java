package com.example.livebettingapp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Bet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long matchId;
    // ev sahibi için 1, deplasman takımı için 2, beraberelik için 0
    private Integer selectedResult;
    private double selectedOdd;
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;
}