package com.gmclabs.datatoolbackend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class TradePieceCompareDatas {
    List<TradePieceCompareData> firstCompareTable;

    List<TradePieceCompareData> secondCompareTable;
}
