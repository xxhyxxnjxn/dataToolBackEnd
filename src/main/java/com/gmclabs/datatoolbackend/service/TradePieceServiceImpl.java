package com.gmclabs.datatoolbackend.service;

import com.gmclabs.datatoolbackend.dto.TradePieceDTO;
import com.gmclabs.datatoolbackend.entity.TradePiece;
import com.gmclabs.datatoolbackend.repository.TradePieceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TradePieceServiceImpl implements TradePieceService{
    private final TradePieceRepository tradePieceRepository;

    private List<TradePiece> tradePieces;

    public void setTradePieces() {
        this.tradePieces = new ArrayList<>();
    }

    public void addTradePieces(TradePiece tradePiece) {
        tradePieces.add(tradePiece);
    }

    public void saveData() {
        tradePieceRepository.saveAll(tradePieces);
    }

    public List<String> getGroupByEntry(String tdNameId) {
        return tradePieceRepository.findGroupByEntry(tdNameId);
    }

    public List<TradePiece> findByTradePieceData(String entry, int tdNameId) {
        return tradePieceRepository.findAllByEntryEqualsAndTradeNameIdEquals(entry, tdNameId);
    }

    public List<TradePieceDTO> findByTradePieceData(int tdNameId) {
        List<TradePiece> tradePieces = tradePieceRepository.findAllByTradeNameIdEquals(tdNameId);
        return tradePieces.stream().map(tradePiece -> entityToDTO(tradePiece)).collect(Collectors.toList());
    }

    public void deleteTradePiece(int tdNameId){
        tradePieceRepository.deleteData(tdNameId);
    }
}
