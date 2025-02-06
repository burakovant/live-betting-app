package com.example.livebettingapp.service;

import com.example.livebettingapp.entity.Match;
import com.example.livebettingapp.entity.Odds;
import com.example.livebettingapp.repository.OddsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledOddsService {

    private final OddsRepository oddsRepository;
    private final OddsService oddsService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    @Scheduled(fixedRate = 1000)
    public void updateOdds() {
        List<Match> matches = oddsRepository.findAll().stream().map(Odds::getMatch).toList();
        for (Match match : matches) {
            oddsService.calculateAndSaveOdds(match);
        }
        messagingTemplate.convertAndSend("/topic/odds", oddsRepository.findAllByOrderByUpdatedAtAsc());
    }
}
