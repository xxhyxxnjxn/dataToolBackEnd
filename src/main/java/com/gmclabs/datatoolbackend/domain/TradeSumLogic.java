package com.gmclabs.datatoolbackend.domain;

import com.gmclabs.datatoolbackend.entity.TradeName;
import com.gmclabs.datatoolbackend.entity.TradePiece;
import com.gmclabs.datatoolbackend.entity.TradeSum;
import com.gmclabs.datatoolbackend.service.TradePieceServiceImpl;
import com.gmclabs.datatoolbackend.service.TradeSumService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeSumLogic {
    private final TradePieceServiceImpl tradePieceService;
    private final TradeSumService tradeSumService;

    public void getGroupByEntry(TradeName tradeName) {
        List<String> groupByEntries = tradePieceService.getGroupByEntry(String.valueOf(tradeName.getId()));
        tradeSumService.setTradeSums();

        for (int i = 0; i < groupByEntries.size(); i++) {
            TradeSum tradeSum = makeTradeSum(tradePieceService.findByTradePieceData(groupByEntries.get(i), tradeName.getId()));
            tradeSum.setTradeName(tradeName);
            tradeSum.setCreateAt(tradeName.getCreateAt());
            tradeSum.setUpdatedAt(tradeName.getUpdatedAt());
            tradeSum.setEntry(groupByEntries.get(i));
            tradeSumService.addTradeSums(tradeSum);
        }
    }

    private TradeSum makeTradeSum(List<TradePiece> tradePieces) {
        TradeSum tradeSum = new TradeSum();
        tradeSum.setTransCount(String.valueOf(tradePieces.size()));
        BigDecimal revenuePer = BigDecimal.ZERO;
        BigDecimal revenueCount = BigDecimal.ZERO;
        BigDecimal lossPer = BigDecimal.ZERO;
        BigDecimal lossCount = BigDecimal.ZERO;
        BigDecimal revenueTotal;
        BigDecimal victoryPer = BigDecimal.ZERO;

        for (TradePiece tradePiece : tradePieces) {
            BigDecimal profitPer = new BigDecimal(tradePiece.getProfitPer());
            if (profitPer.compareTo(BigDecimal.ZERO) > 0) {
                revenuePer = revenuePer.add(profitPer);
                revenueCount = revenueCount.add(BigDecimal.ONE);
            } else if (profitPer.compareTo(BigDecimal.ZERO) < 0) {
                lossPer = lossPer.add(profitPer);
                lossCount = lossCount.add(BigDecimal.ONE);
            }
        }

        revenueTotal = revenuePer.add(lossPer);

        if (revenueCount.compareTo(BigDecimal.ZERO) > 0) {
            victoryPer = revenueCount.divide(revenueCount.add(lossCount), 8, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100.0));
        }

        tradeSum.setRevenuePer(revenuePer.toEngineeringString());
        tradeSum.setLossPer(lossPer.toEngineeringString());
        tradeSum.setRevenueTotal(revenueTotal.toEngineeringString());
        tradeSum.setTransCount(String.valueOf(tradePieces.size()));
        tradeSum.setRevenueCount(String.valueOf(revenueCount));
        tradeSum.setLossCount(String.valueOf(lossCount));
        tradeSum.setVictoryPer(victoryPer.toEngineeringString());
        return tradeSum;
    }

    public void saveTradeSumData() {
        tradeSumService.saveData();
    }
}
