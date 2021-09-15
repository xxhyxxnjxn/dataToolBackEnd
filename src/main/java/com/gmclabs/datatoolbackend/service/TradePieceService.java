package com.gmclabs.datatoolbackend.service;

import com.gmclabs.datatoolbackend.dto.TradePieceDTO;
import com.gmclabs.datatoolbackend.entity.TradePiece;

import java.util.List;

public interface TradePieceService {
    void setTradePieces();

    void addTradePieces(TradePiece tradePiece);

    void saveData();

    List<String> getGroupByEntry(String tdNameId);

    List<TradePiece> findByTradePieceData(String entry, int tdNameId);

    List<TradePieceDTO> findByTradePieceData(int tdNameId);

    void deleteTradePiece(int tdNameId);

    default TradePieceDTO entityToDTO(TradePiece tradePiece) {
        TradePieceDTO tradePieceDTO = TradePieceDTO.builder()
                .id(tradePiece.getId())
                .tradeName(tradePiece.getTradeName())
                .tdId(tradePiece.getTdId())
                .start(tradePiece.getStart())
                .entry(tradePiece.getEntry())
                .end(tradePiece.getEnd())
                .profitPer(tradePiece.getProfitPer())
                .runupPer(tradePiece.getRunupPer())
                .drawdownPer(tradePiece.getDrawdownPer())
                .createAt(tradePiece.getCreateAt())
                .updatedAt(tradePiece.getUpdatedAt())
                .memo(tradePiece.getMemo())
                .build();

        return tradePieceDTO;
    }
}
