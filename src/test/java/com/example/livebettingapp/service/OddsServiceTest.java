package com.example.livebettingapp.service;

import com.example.livebettingapp.dto.OddsHistoryDto;
import com.example.livebettingapp.entity.Match;
import com.example.livebettingapp.entity.Odds;
import com.example.livebettingapp.entity.OddsHistory;
import com.example.livebettingapp.exception.BettingException;
import com.example.livebettingapp.mapper.OddsHistoryMapper;
import com.example.livebettingapp.repository.OddsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OddsServiceTest {

    @Mock
    private OddsRepository oddsRepository;

    @Mock
    private OddsHistoryMapper oddsHistoryMapper;

    @InjectMocks
    private OddsService oddsService;

    /**
     * Bir maç ID'sine göre birden fazla Odds geçmişini eskiden yeniye doğru sırada
     * getirme işlevselliğini test eder.
     * <p>
     * Bu test aşağıdaki durumları doğrular:
     * 1. Bir maç ID'si sağlandığında, repository ilgili Odds varlığını getirir.
     * 2. Eğer Odds varlığı birden fazla OddsHistory varlığı içeriyorsa, bunlar mapper aracılığıyla
     * doğru şekilde OddsHistoryDto nesnelerine dönüştürülür.
     * 3. Ortaya çıkan OddsHistoryDto nesnelerinin listesi, beklenen boyut ve sırayla eşleşir.
     * 4. Repository ve mapper metodlarının uygun sayıda çağrıldığından emin olur.
     */
    @Test
    void testGetOddsHistoryByMatchId_MultipleHistories() {
        Long matchId = 1L;
        Odds odds = new Odds();
        OddsHistory oh1 = new OddsHistory();
        OddsHistory oh2 = new OddsHistory();
        odds.setOddsHistories(List.of(oh1, oh2));

        OddsHistoryDto dto1 = new OddsHistoryDto();
        OddsHistoryDto dto2 = new OddsHistoryDto();

        when(oddsRepository.findByMatchId(matchId)).thenReturn(Optional.of(odds));
        when(oddsHistoryMapper.toDto(oh1)).thenReturn(dto1);
        when(oddsHistoryMapper.toDto(oh2)).thenReturn(dto2);

        List<OddsHistoryDto> result = oddsService.getOddsHistoryByMatchId(matchId);

        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Result size should match odds histories size");
        assertEquals(dto1, result.get(0), "First DTO should match the first odds history");
        assertEquals(dto2, result.get(1), "Second DTO should match the second odds history");
        verify(oddsRepository, times(1)).findByMatchId(matchId);
        verify(oddsHistoryMapper, times(2)).toDto(oh1);
        verify(oddsHistoryMapper, times(2)).toDto(oh2);
    }

    @Test
    void testGetOddsById_Found() {
        Long id = 1L;
        Odds odds = new Odds();
        odds.setId(id);

        when(oddsRepository.findById(id)).thenReturn(Optional.of(odds));

        Odds result = oddsService.getOddsById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(oddsRepository, times(1)).findById(id);
    }

    @Test
    void testGetOddsById_NotFound() {
        Long id = 1L;

        when(oddsRepository.findById(id)).thenReturn(Optional.empty());

        BettingException exception = assertThrows(BettingException.class, () -> oddsService.getOddsById(id));

        assertEquals("Odds not found", exception.getMessage());
        verify(oddsRepository, times(1)).findById(id);
    }

    @Test
    void testCalculateAndSaveOdds_NewOdds() {
        Match match = new Match();
        match.setId(1L);

        when(oddsRepository.findByMatchId(match.getId())).thenReturn(Optional.empty());

        oddsService.calculateAndSaveOdds(match);

        ArgumentCaptor<Odds> oddsCaptor = ArgumentCaptor.forClass(Odds.class);
        verify(oddsRepository, times(1)).save(oddsCaptor.capture());

        Odds savedOdds = oddsCaptor.getValue();
        assertNotNull(savedOdds);
        assertEquals(match, savedOdds.getMatch());
        assertNotNull(savedOdds.getUpdatedAt());
    }

    @Test
    void testCalculateAndSaveOdds_ExistingOdds() {
        Match match = new Match();
        match.setId(1L);

        Odds existingOdds = new Odds();
        existingOdds.setMatch(match);
        List<OddsHistory> oddsHistories = new ArrayList<>();
        oddsHistories.add(new OddsHistory());
        existingOdds.setOddsHistories(oddsHistories);

        when(oddsRepository.findByMatchId(match.getId())).thenReturn(Optional.of(existingOdds));

        oddsService.calculateAndSaveOdds(match);

        assertNotNull(existingOdds.getUpdatedAt());
        assertFalse(existingOdds.getOddsHistories().isEmpty());
        verify(oddsRepository, times(1)).save(existingOdds);
    }

    @Test
    void testGetOddsHistoryByMatchId_Found() {
        Long matchId = 1L;
        Odds odds = new Odds();
        odds.setId(2L);
        OddsHistory oddsHistory = new OddsHistory();
        odds.setOddsHistories(List.of(oddsHistory));

        OddsHistoryDto oddsHistoryDto = new OddsHistoryDto();

        when(oddsRepository.findByMatchId(matchId)).thenReturn(Optional.of(odds));
        when(oddsHistoryMapper.toDto(any())).thenReturn(oddsHistoryDto);

        List<OddsHistoryDto> result = oddsService.getOddsHistoryByMatchId(matchId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(oddsHistoryDto, result.get(0));
        verify(oddsRepository, times(1)).findByMatchId(matchId);
        verify(oddsHistoryMapper, times(1)).toDto(oddsHistory);
    }

    @Test
    void testGetOddsHistoryByMatchId_NotFound() {
        Long matchId = 1L;

        when(oddsRepository.findByMatchId(matchId)).thenReturn(Optional.empty());

        BettingException exception = assertThrows(
                BettingException.class,
                () -> oddsService.getOddsHistoryByMatchId(matchId),
                "Expected BettingException when odds not found for match ID"
        );

        assertEquals("Odds not found for match ID: " + matchId, exception.getMessage(), "Incorrect exception message");
        verify(oddsRepository, times(1)).findByMatchId(matchId);
        verifyNoInteractions(oddsHistoryMapper);
    }

    @Test
    void testGetOddsHistoryByMatchId_EmptyOddsHistories() {
        Long matchId = 1L;
        Odds odds = new Odds();
        odds.setOddsHistories(List.of());

        when(oddsRepository.findByMatchId(matchId)).thenReturn(Optional.of(odds));

        List<OddsHistoryDto> result = oddsService.getOddsHistoryByMatchId(matchId);

        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Odds history should be empty for empty histories list");
        verify(oddsRepository, times(1)).findByMatchId(matchId);
        verifyNoInteractions(oddsHistoryMapper);
    }

    @Test
    void testGetOddsHistoryByMatchId_NullMatchId() {
        BettingException exception = assertThrows(
                BettingException.class,
                () -> oddsService.getOddsHistoryByMatchId(null),
                "Expected BettingException for null match ID"
        );

        assertEquals("Odds not found for match ID: null", exception.getMessage(), "Incorrect exception message");
        verifyNoInteractions(oddsHistoryMapper);
    }
}