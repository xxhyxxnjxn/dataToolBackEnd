package com.gmclabs.datatoolbackend.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TradePieceCompareData {
    String start;
    String end;
    String entry;
    String profitPer; // 수익률
    String runupPer; // 고점
    String drawdownPer; // 저점
}
