package com.example.livebettingapp.service;

import com.example.livebettingapp.dto.MatchDto;
import com.example.livebettingapp.entity.Match;
import com.example.livebettingapp.entity.Odds;
import com.example.livebettingapp.exception.BettingException;
import com.example.livebettingapp.mapper.MatchMapper;
import com.example.livebettingapp.repository.MatchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @InjectMocks
    private MatchService matchService;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private OddsService oddsService;

    @Mock
    private MatchMapper matchMapper;

    @Test
    void createMatchWithOdds_successfulCreation() {
        MatchDto matchDto = new MatchDto();
        matchDto.setOddsId(1L);

        Match mappedMatch = new Match();
        when(matchMapper.toEntity(matchDto)).thenReturn(mappedMatch);
        Odds odds = new Odds();
        odds.setId(1L);
        odds.setHomeWin(2.5);
        when(oddsService.getOddsById(1L)).thenReturn(odds);
        when(matchRepository.save(any(Match.class))).thenReturn(mappedMatch);

        matchService.createMatchWithOdds(matchDto);

        verify(matchMapper).toEntity(matchDto);
        verify(oddsService).getOddsById(1L);
        verify(matchRepository).save(any(Match.class));
        verify(oddsService).calculateAndSaveOdds(mappedMatch);
    }

    @Test
    void createMatchWithOdds_throwsExceptionWhenMatchDtoIsNull() {
        assertThrows(BettingException.class, () -> matchService.createMatchWithOdds(null));
        verifyNoInteractions(oddsService, matchRepository);
    }

    @Test
    void createMatchWithOdds_throwsExceptionWhenOddsIdIsNull() {
        MatchDto matchDto = new MatchDto();

        when(matchMapper.toEntity(matchDto)).thenThrow(new IllegalArgumentException("Odds ID cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> matchService.createMatchWithOdds(matchDto));

        verify(matchMapper).toEntity(matchDto);
        verifyNoInteractions(oddsService, matchRepository);
    }

    @Test
    void createMatchWithOdds_matchMapperReturnsNull() {
        MatchDto matchDto = new MatchDto();
        matchDto.setOddsId(1L);

        when(matchMapper.toEntity(matchDto)).thenReturn(null);

        assertThrows(BettingException.class, () -> matchService.createMatchWithOdds(matchDto));

        verify(matchMapper).toEntity(matchDto);
        verifyNoInteractions(oddsService, matchRepository);
    }

    @Test
    void createMatchWithOdds_calculateAndSaveOddsThrowsException() {
        MatchDto matchDto = new MatchDto();
        matchDto.setOddsId(1L);

        Match mappedMatch = new Match();
        when(matchMapper.toEntity(matchDto)).thenReturn(mappedMatch);
        Odds odds = new Odds();
        odds.setId(1L);
        odds.setHomeWin(2.5);
        when(oddsService.getOddsById(1L)).thenReturn(odds);
        when(matchRepository.save(any(Match.class))).thenReturn(mappedMatch);
        doThrow(new RuntimeException("Calculation failed")).when(oddsService).calculateAndSaveOdds(mappedMatch);

        assertThrows(RuntimeException.class, () -> matchService.createMatchWithOdds(matchDto));
    }

    @Test
    void createMatchWithOdds_throwsExceptionWhenOddsNotFound() {
        MatchDto matchDto = new MatchDto();
        matchDto.setOddsId(999L);

        Match mappedMatch = new Match();
        when(matchMapper.toEntity(matchDto)).thenReturn(mappedMatch);
        when(oddsService.getOddsById(999L)).thenThrow(new BettingException("Odds not found"));

        assertThrows(BettingException.class, () -> matchService.createMatchWithOdds(matchDto));

        verify(matchMapper).toEntity(matchDto);
        verify(oddsService).getOddsById(999L);
        verify(matchRepository, times(0)).save(any(Match.class));
        verify(oddsService, times(0)).calculateAndSaveOdds(any());
    }

    @Test
    void createMatchWithOdds_repositorySaveFails() {
        MatchDto matchDto = new MatchDto();
        matchDto.setOddsId(1L);

        Match mappedMatch = new Match();
        when(matchMapper.toEntity(matchDto)).thenReturn(mappedMatch);
        Odds odds = new Odds();
        odds.setId(1L);
        odds.setHomeWin(2.5);
        when(oddsService.getOddsById(1L)).thenReturn(odds);
        when(matchRepository.save(any(Match.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> matchService.createMatchWithOdds(matchDto));

        verify(matchMapper).toEntity(matchDto);
        verify(oddsService).getOddsById(1L);
        verify(matchRepository).save(any(Match.class));
        verify(oddsService, times(0)).calculateAndSaveOdds(any());
    }
}