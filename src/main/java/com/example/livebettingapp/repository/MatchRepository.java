package com.example.livebettingapp.repository;

import com.example.livebettingapp.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {
}
