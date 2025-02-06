package com.example.livebettingapp.mapper;

import com.example.livebettingapp.dto.MatchDto;
import com.example.livebettingapp.entity.Match;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MatchMapper {
    public MatchDto toDto(Match match) {
        MatchDto dto = new MatchDto();
        dto.setId(match.getId());
        dto.setLeague(match.getLeague());
        dto.setHomeTeam(match.getHomeTeam());
        dto.setAwayTeam(match.getAwayTeam());
        dto.setStartTime(match.getStartTime());
        dto.setBetCount(match.getBetCount());
        dto.setOddsId(match.getOdds().getId());
        return dto;
    }

    public Match toEntity(MatchDto dto) {
        Match match = new Match();
        match.setId(dto.getId());
        match.setLeague(dto.getLeague());
        match.setHomeTeam(dto.getHomeTeam());
        match.setAwayTeam(dto.getAwayTeam());
        match.setStartTime(dto.getStartTime());
        match.setBetCount(dto.getBetCount());
        return match;
    }

    public List<MatchDto> toDtos(List<Match> matches) {
        return matches.stream().map(this::toDto).toList();
    }
}