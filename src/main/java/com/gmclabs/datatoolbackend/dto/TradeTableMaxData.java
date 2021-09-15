package com.gmclabs.datatoolbackend.dto;

import com.gmclabs.datatoolbackend.domain.TradeTableDatas;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TradeTableMaxData {
    TradeTableDatas tradeTableMaxLossPerData;
    TradeTableDatas tradeTableMaxRevenuePerData;
    TradeTableDatas tradeTableMaxVictoryPerData;
}
