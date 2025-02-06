package com.example.livebettingapp.controller;

import com.example.livebettingapp.dto.MatchDto;
import com.example.livebettingapp.dto.OddsHistoryDto;
import com.example.livebettingapp.service.MatchService;
import com.example.livebettingapp.service.OddsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
class MatchController {
    private final MatchService matchService;
    private final OddsService oddsService;

    @PostMapping
    public void addMatch(@RequestBody MatchDto match) {
        matchService.createMatchWithOdds(match);
    }

    @GetMapping
    public List<MatchDto> getMatches() {
        return matchService.getMatches();
    }

    @GetMapping("/{id}")
    public MatchDto getMatchById(@PathVariable Long id) {
        return matchService.getMatchDtoById(id);
    }

    @GetMapping("/{id}/odds-history")
    public List<OddsHistoryDto> getOddsHistoryByMatchId(@PathVariable Long id) {
        return oddsService.getOddsHistoryByMatchId(id);
    }
}
