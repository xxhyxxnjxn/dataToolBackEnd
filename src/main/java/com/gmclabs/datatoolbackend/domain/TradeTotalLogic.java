package com.gmclabs.datatoolbackend.domain;

import com.gmclabs.datatoolbackend.entity.TradeName;
import com.gmclabs.datatoolbackend.entity.TradeTotal;
import com.gmclabs.datatoolbackend.service.TradeSumService;
import com.gmclabs.datatoolbackend.service.TradeTotalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TradeTotalLogic {
    private final TradeSumService tradeSumService;
    private final TradeTotalService tradeTotalService;

    private TradeName tradeName;
    private TotalSumLikeEntry tradeSumsLikeLong;
    private TotalSumLikeEntry tradeSumsLikeShort;
    private TotalSumLikeEntry tradeSumsLikeLongOrShort;

    public void setTradeName(TradeName tradeName) {
        this.tradeName = tradeName;
    }

    public void calculateTradeTotal() {
        this.tradeSumsLikeLong = tradeSumService.getContainsLongSentence(tradeName.getId());
        this.tradeSumsLikeShort = tradeSumService.getContainsShortSentence(tradeName.getId());
        this.tradeSumsLikeLongOrShort = tradeSumService.getContainsShortOrLongSentence(tradeName.getId());
    }

    public void makeTradeTotal() {
        tradeTotalService.setTradeTotals();

        TradeTotal tradeTotalLong = makeTradeTotalToTotalSumLikeEntry(tradeSumsLikeLong);
        tradeTotalLong.setCategory("long");
        tradeTotalService.addTradeTotal(tradeTotalLong);

        TradeTotal tradeTotalShort = makeTradeTotalToTotalSumLikeEntry(tradeSumsLikeShort);
        tradeTotalShort.setCategory("short");
        tradeTotalService.addTradeTotal(tradeTotalShort);

        TradeTotal tradeTotalLongOrShort = makeTradeTotalToTotalSumLikeEntry(tradeSumsLikeLongOrShort);
        tradeTotalLongOrShort.setCategory("total");
        tradeTotalService.addTradeTotal(tradeTotalLongOrShort);
    }

    private TradeTotal makeTradeTotalToTotalSumLikeEntry(TotalSumLikeEntry totalSumLikeEntry) {
        TradeTotal tradeTotal = new TradeTotal();
        tradeTotal.setTradeName(tradeName);
        tradeTotal.setRevenuePer(totalSumLikeEntry.getRevenuePer().toEngineeringString());
        tradeTotal.setLossPer(totalSumLikeEntry.getLossPer().toEngineeringString());
        tradeTotal.setRevenueTotal(totalSumLikeEntry.getRevenueTotal().toEngineeringString());
        tradeTotal.setTransCount(totalSumLikeEntry.getTransCount().toEngineeringString());
        tradeTotal.setRevenueCount(totalSumLikeEntry.getRevenueCount().toEngineeringString());
        tradeTotal.setLossCount(totalSumLikeEntry.getLossCount().toEngineeringString());
        tradeTotal.setVictoryPer(totalSumLikeEntry.getVictoryPer().toEngineeringString());
        tradeTotal.setCreateAt(tradeName.getCreateAt());
        tradeTotal.setUpdatedAt(tradeName.getUpdatedAt());
        return tradeTotal;
    }

    public void saveTradeTotalData() {
        tradeTotalService.saveData();
    }
}
