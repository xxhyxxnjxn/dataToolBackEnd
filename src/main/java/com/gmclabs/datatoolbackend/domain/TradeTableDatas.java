package com.gmclabs.datatoolbackend.domain;

import com.gmclabs.datatoolbackend.dto.TradePieceDTO;
import com.gmclabs.datatoolbackend.dto.TradeSumDTO2;
import com.gmclabs.datatoolbackend.entity.TradePiece;
import com.gmclabs.datatoolbackend.entity.TradeSum;
import com.gmclabs.datatoolbackend.entity.TradeTotal;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TradeTableDatas {
    TradeTotal tradeTotal;
    TradeTotal tradeTotalLong;
    TradeTotal tradeTotalShort;
    List<TradeSum> tradeSumLongs;
    List<TradeSum> tradeSumShorts;
    List<TradePieceDTO> tradePieces;
    String interval;
}
