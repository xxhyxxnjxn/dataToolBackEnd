package com.gmclabs.datatoolbackend.service;

import com.gmclabs.datatoolbackend.entity.TradeName;
import com.gmclabs.datatoolbackend.repository.TradeNameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradeNameService {
    private final TradeNameRepository tradeNameRepository;

    public void saveData(String title) {
        Date date = new Date();
        TradeName tradeName = new TradeName();

        tradeName.setTdName(title);
        tradeName.setCreateAt(date);
        tradeName.setUpdatedAt(date);
        tradeNameRepository.save(tradeName);
    }

    public TradeName selectTitle(String title) {
        return tradeNameRepository.findByTdName(title);
    }

    public List<TradeName> findAll(){
        return tradeNameRepository.findAll();
    }

    public void deleteTradeName(int id){
        tradeNameRepository.deleteData(id);
    }
}
