package com.example.livebettingapp.mapper;

import com.example.livebettingapp.dto.OddsHistoryDto;
import com.example.livebettingapp.entity.OddsHistory;
import org.springframework.stereotype.Component;

@Component
public class OddsHistoryMapper {

    public OddsHistoryDto toDto(OddsHistory oddsHistory) {
        OddsHistoryDto dto = new OddsHistoryDto();
        dto.setId(oddsHistory.getId());
        dto.setHomeWin(oddsHistory.getHomeWin());
        dto.setDraw(oddsHistory.getDraw());
        dto.setAwayWin(oddsHistory.getAwayWin());
        dto.setUpdatedAt(oddsHistory.getUpdatedAt());
        return dto;
    }

}
