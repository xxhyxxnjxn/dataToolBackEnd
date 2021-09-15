package com.gmclabs.datatoolbackend.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class TotalSumLikeEntry {
    private BigDecimal revenuePer;
    private BigDecimal lossPer;
    private BigDecimal revenueTotal;
    private BigDecimal transCount;
    private BigDecimal revenueCount;
    private BigDecimal lossCount;
    private BigDecimal victoryPer;

    public TotalSumLikeEntry() {
        this.revenuePer = BigDecimal.ZERO;
        this.lossPer = BigDecimal.ZERO;
        this.revenueTotal = BigDecimal.ZERO;
        this.transCount = BigDecimal.ZERO;
        this.revenueCount = BigDecimal.ZERO;
        this.lossCount = BigDecimal.ZERO;
        this.victoryPer = BigDecimal.ZERO;
    }

    public void accumulator(String revenuePer, String lossPer, String revenueTotal, String transCount, String revenueCount, String lossCount) {
        this.revenuePer = this.revenuePer.add(new BigDecimal(revenuePer));
        this.lossPer = this.lossPer.add(new BigDecimal(lossPer));
        this.revenueTotal = this.revenueTotal.add(new BigDecimal(revenueTotal));
        this.transCount = this.transCount.add(new BigDecimal(transCount));
        this.revenueCount = this.revenueCount.add(new BigDecimal(revenueCount));
        this.lossCount = this.lossCount.add(new BigDecimal(lossCount));
    }

    public void calculateVictoryPer() {
        this.victoryPer = revenueCount.divide(revenueCount.add(lossCount), 8, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100.0));
    }
}
