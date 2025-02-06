package com.example.livebettingapp.repository;

import com.example.livebettingapp.entity.Bet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BetRepository extends JpaRepository<Bet, Long> {
}
