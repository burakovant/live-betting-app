package com.example.livebettingapp.service;

import com.example.livebettingapp.dto.OddsHistoryDto;
import com.example.livebettingapp.entity.Match;
import com.example.livebettingapp.entity.Odds;
import com.example.livebettingapp.entity.OddsHistory;
import com.example.livebettingapp.exception.BettingException;
import com.example.livebettingapp.mapper.OddsHistoryMapper;
import com.example.livebettingapp.repository.OddsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@EnableWebSocketMessageBroker
@Service
@Slf4j
@RequiredArgsConstructor
public class OddsService {
    private final OddsRepository oddsRepository;
    private final OddsHistoryMapper oddsHistoryMapper;

    private final Random random = new Random();

    @Value("${betting.margin.percent:20}")
    private double margin; // application.properties'ten okunan kar marjı

    public Odds getOddsById(Long id) {
        return oddsRepository.findById(id).orElseThrow(() -> new BettingException("Odds not found"));
    }

    @Transactional(rollbackFor = Throwable.class)
    public void calculateAndSaveOdds(Match match) {
        // Veritabanında mevcut kayıt kontrolü
        Optional<Odds> existingOdds = oddsRepository.findByMatchId(match.getId());

        Odds odds;
        if (existingOdds.isPresent()) {
            // Mevcut kaydı güncellemeden önce eski halini oddshistory tablosuna ekleyelim.
            odds = existingOdds.get();
            OddsHistory oddsHistory = new OddsHistory();
            oddsHistory.setOdds(odds);
            oddsHistory.setDraw(odds.getDraw());
            oddsHistory.setHomeWin(odds.getHomeWin());
            oddsHistory.setAwayWin(odds.getAwayWin());
            oddsHistory.setUpdatedAt(odds.getUpdatedAt());
            odds.getOddsHistories().add(oddsHistory);
        } else {
            // Yeni kayıt oluşturun
            odds = new Odds();
            odds.setMatch(match);
        }
        calculateOdds(odds);
        oddsRepository.save(odds);
    }

    /**
     * Belirtilen bir maç için bahis oranlarını hesaplar ve verilen Odds nesnesini,
     * rastgeleleştirilmiş olasılıklar ve önceden tanımlanmış bir kar marjına dayanarak günceller.
     *
     * @param odds Ev sahibi galibiyeti, beraberlik ve deplasman galibiyeti için hesaplanan değerlerle
     *             güncellenecek olan Odds nesnesi
     */
    private void calculateOdds(Odds odds) {
        // kolaylık açısından ortalamalar varsayıldı
        double probabilityHomeWin = 40 + (random.nextDouble() * 30 - 15); // ortalama 40, 25-55 arası
        double probabilityDraw = 30 + (random.nextDouble() * 10 - 5); // ortalama 30, 25-35 arası
        double probabilityAwayWin = 100 - (probabilityHomeWin + probabilityDraw);

        // Kazancı garanti etmemek üzerine tüm olasılıkların toplamından margin yani kar marjı düşülür.
        double adjustedMargin = (100 - margin);

        double homeWinOdds = adjustedMargin / probabilityHomeWin;
        double drawOdds = adjustedMargin / probabilityDraw;
        double awayWinOdds = adjustedMargin / probabilityAwayWin;

        odds.setHomeWin(Math.round(homeWinOdds * 100.0) / 100.0);
        odds.setDraw(Math.round(drawOdds * 100.0) / 100.0);
        odds.setAwayWin(Math.round(awayWinOdds * 100.0) / 100.0);
        odds.setUpdatedAt(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<OddsHistoryDto> getOddsHistoryByMatchId(Long matchId) {
        return oddsRepository.findByMatchId(matchId)
                .map(odds -> odds.getOddsHistories().stream()
                        .map(oddsHistoryMapper::toDto)
                        .toList())
                .orElseThrow(() -> new BettingException("Odds not found for match ID: " + matchId));
    }
}
