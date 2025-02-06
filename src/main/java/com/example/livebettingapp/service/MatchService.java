package com.example.livebettingapp.service;

import com.example.livebettingapp.dto.MatchDto;
import com.example.livebettingapp.exception.BettingException;
import com.example.livebettingapp.mapper.MatchMapper;
import com.example.livebettingapp.entity.Match;
import com.example.livebettingapp.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchService {
    private final MatchRepository matchRepository;
    private final OddsService oddsService;
    private final MatchMapper matchMapper;

    public Match getMatchById(Long id) {
        return matchRepository.findById(id).orElse(null);
    }

    public MatchDto getMatchDtoById(Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new BettingException("Match with ID " + id + " not found"));
        return matchMapper.toDto(match);
    }

    public List<MatchDto> getMatches() {
        return matchMapper.toDtos(matchRepository.findAll());
    }

    public void createMatchWithOdds(MatchDto matchDto) {
        Match match = matchMapper.toEntity(matchDto);
        if (match == null) {
            throw new BettingException("Failed to map MatchDto to Match entity");
        }

        match.setOdds(oddsService.getOddsById(matchDto.getOddsId()));
        match = matchRepository.save(match);
        oddsService.calculateAndSaveOdds(match);
    }

    public void incrementBetCount(Long id) {
        Match match = getMatchById(id);
        match.setBetCount(match.getBetCount() + 1);
        matchRepository.save(match);
    }
}