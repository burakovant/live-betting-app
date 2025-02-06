package com.example.livebettingapp;

import com.example.livebettingapp.entity.Match;
import com.example.livebettingapp.entity.Odds;
import com.example.livebettingapp.repository.MatchRepository;
import com.example.livebettingapp.repository.OddsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.stream.IntStream;

@SpringBootApplication
@EnableScheduling
public class LiveBettingApplication {
    public static void main(String[] args) {
        SpringApplication.run(LiveBettingApplication.class, args);
    }

    // ayağa kalkarken örnek bir bülten maç datalarını oluşturalım.
    @Bean
    CommandLineRunner initDatabase(MatchRepository matchRepository, OddsRepository oddsRepository) {
        return args -> {
            Random random = new Random();
            String[] leagues = {"Premier League", "La Liga", "Bundesliga", "Serie A", "Ligue 1"};
            String[] teams = {"Team A", "Team B", "Team C", "Team D", "Team E", "Team F", "Team G", "Team H"};

            IntStream.range(0, 100).forEach(i -> {
                Match match = new Match();
                match.setLeague(leagues[random.nextInt(leagues.length)]);
                match.setHomeTeam(teams[random.nextInt(teams.length)]);
                match.setAwayTeam(teams[random.nextInt(teams.length)]);
                match.setStartTime(LocalDateTime.now().plusDays(random.nextInt(10)));

                match = matchRepository.save(match);

                Odds odds = new Odds();
                odds.setMatch(match);
                odds.setHomeWin(1.5 + random.nextDouble());
                odds.setDraw(2.0 + random.nextDouble());
                odds.setAwayWin(2.5 + random.nextDouble());
                odds.setUpdatedAt(LocalDateTime.now());

                oddsRepository.save(odds);
            });
        };
    }

}

