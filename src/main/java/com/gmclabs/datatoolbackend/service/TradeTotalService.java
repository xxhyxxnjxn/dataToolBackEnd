package com.gmclabs.datatoolbackend.service;

import com.gmclabs.datatoolbackend.entity.TradeTotal;
import com.gmclabs.datatoolbackend.repository.TradeTotalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeTotalService {
    private final TradeTotalRepository tradeTotalRepository;

    private List<TradeTotal> tradeTotals;

    public void setTradeTotals() {
        this.tradeTotals = new ArrayList<>();
    }

    public void addTradeTotal(TradeTotal tradeTotal) {
        this.tradeTotals.add(tradeTotal);
    }

    public void saveData() {
        tradeTotalRepository.saveAll(tradeTotals);
    }

    public TradeTotal findByCategoryTotal(String category,int tdNameId){
        return tradeTotalRepository.findByCategoryEqualsAndTradeNameIdEquals(category,tdNameId);
    }

    public void deleteTradeTotal(int tdNameId){
        tradeTotalRepository.deleteData(tdNameId);
    }
}
