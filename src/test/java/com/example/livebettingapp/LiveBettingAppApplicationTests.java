package com.example.livebettingapp;

import com.example.livebettingapp.entity.Bet;
import com.example.livebettingapp.entity.Match;
import com.example.livebettingapp.repository.BetRepository;
import com.example.livebettingapp.repository.MatchRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
class LiveBettingApplicationTests {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private BetRepository betRepository;

    @Test
    void testMatchCreation() {
        Match match = new Match();
        match.setLeague("Premier League");
        match.setHomeTeam("Team A");
        match.setAwayTeam("Team B");
        match.setStartTime(LocalDateTime.now());

        Match savedMatch = matchRepository.save(match);
        Assertions.assertNotNull(savedMatch.getId());
    }

    @Test
    void testBetPlacement() {
        Bet bet = new Bet();
        bet.setMatchId(1L);
        bet.setSelectedResult(2);
        bet.setSelectedOdd(2.5);
        bet.setTimestamp(LocalDateTime.now());

        Bet savedBet = betRepository.save(bet);
        Assertions.assertNotNull(savedBet.getId());
    }
}
