package com.gmclabs.datatoolbackend.service;

import com.gmclabs.datatoolbackend.domain.TotalSumLikeEntry;
import com.gmclabs.datatoolbackend.entity.TradeSum;
import com.gmclabs.datatoolbackend.repository.TradeSumRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeSumService {
    private final TradeSumRepository tradeSumRepository;

    private List<TradeSum> tradeSums;

    public void setTradeSums() {
        this.tradeSums = new ArrayList<>();
    }

    public void addTradeSums(TradeSum tradeSum) {
        tradeSums.add(tradeSum);
    }

    public void saveData() {
        tradeSumRepository.saveAll(tradeSums);
    }

    public TotalSumLikeEntry getContainsLongSentence(int tdNameId) {
        List<TradeSum> tradeSums = findEntryLikeLong(tdNameId);
        TotalSumLikeEntry totalSumLikeEntry = new TotalSumLikeEntry();
        tradeSums.stream()
                .forEach(tradeSum -> totalSumLikeEntry.accumulator(tradeSum.getRevenuePer(), tradeSum.getLossPer(), tradeSum.getRevenueTotal(), tradeSum.getTransCount(), tradeSum.getRevenueCount(), tradeSum.getLossCount()));
        totalSumLikeEntry.calculateVictoryPer();
        return totalSumLikeEntry;
    }

    public TotalSumLikeEntry getContainsShortSentence(int tdNameId) {
        List<TradeSum> tradeSums = findEntryLikeShort(tdNameId);
        TotalSumLikeEntry totalSumLikeEntry = new TotalSumLikeEntry();
        tradeSums.stream()
                .forEach(tradeSum -> totalSumLikeEntry.accumulator(tradeSum.getRevenuePer(), tradeSum.getLossPer(), tradeSum.getRevenueTotal(), tradeSum.getTransCount(), tradeSum.getRevenueCount(), tradeSum.getLossCount()));
        totalSumLikeEntry.calculateVictoryPer();
        return totalSumLikeEntry;
    }

    public TotalSumLikeEntry getContainsShortOrLongSentence(int tdNameId) {
        List<TradeSum> tradeSums = tradeSumRepository.findAllByEntryLikeTotalAndTradeNameIdEquals(tdNameId);
        TotalSumLikeEntry totalSumLikeEntry = new TotalSumLikeEntry();
        tradeSums.stream()
                .forEach(tradeSum -> totalSumLikeEntry.accumulator(tradeSum.getRevenuePer(), tradeSum.getLossPer(), tradeSum.getRevenueTotal(), tradeSum.getTransCount(), tradeSum.getRevenueCount(), tradeSum.getLossCount()));
        totalSumLikeEntry.calculateVictoryPer();
        return totalSumLikeEntry;
    }

    public List<TradeSum> findEntryLikeLong(int tdNameId){
        return tradeSumRepository.findAllByEntryLikeLongAndTradeNameIdEquals(tdNameId);
    }

    public List<TradeSum> findEntryLikeShort(int tdNameId){
        return tradeSumRepository.findAllByEntryLikeShortAndTradeNameIdEquals(tdNameId);
    }

    public void deleteTradeSum(int tdNameId){
        tradeSumRepository.deleteData(tdNameId);
    }

    public int findByTdNameId(HashMap<String, String> data){
        return tradeSumRepository.findByEntryEqualsAndIdEquals(data.get("entry"),Integer.parseInt(data.get("id"))).getTradeName().getId();
    }
}
