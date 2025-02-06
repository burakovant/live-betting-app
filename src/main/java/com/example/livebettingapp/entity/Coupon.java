package com.example.livebettingapp.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL)
    private List<Bet> bets;

}
