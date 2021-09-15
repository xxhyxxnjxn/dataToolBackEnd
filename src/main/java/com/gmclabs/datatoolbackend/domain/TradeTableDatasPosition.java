package com.gmclabs.datatoolbackend.domain;

import com.gmclabs.datatoolbackend.dto.TradePieceDTO;
import com.gmclabs.datatoolbackend.dto.TradeSumDTO2;
import com.gmclabs.datatoolbackend.entity.TradeSum;
import com.gmclabs.datatoolbackend.entity.TradeTotal;
import lombok.Data;

import java.util.List;

@Data
public class TradeTableDatasPosition {
    TradeTotal tradeTotal;
    TradeTotal tradeTotalLong;
    TradeTotal tradeTotalShort;
    List<TradeSumDTO2> tradeSumLongs;
    List<TradeSumDTO2> tradeSumShorts;
}
