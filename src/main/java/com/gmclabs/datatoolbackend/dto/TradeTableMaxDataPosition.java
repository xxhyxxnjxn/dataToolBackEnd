package com.gmclabs.datatoolbackend.dto;

import com.gmclabs.datatoolbackend.domain.TradeTableDatas;
import com.gmclabs.datatoolbackend.domain.TradeTableDatasPosition;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TradeTableMaxDataPosition {
    TradeTableDatasPosition tradeTableMaxLossPerData;
    TradeTableDatasPosition tradeTableMaxRevenuePerData;
    TradeTableDatasPosition tradeTableMaxVictoryPerData;
}
