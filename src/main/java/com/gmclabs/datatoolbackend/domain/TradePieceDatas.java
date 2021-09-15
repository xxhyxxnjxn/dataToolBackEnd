package com.gmclabs.datatoolbackend.domain;

import com.gmclabs.datatoolbackend.entity.TradeName;
import com.gmclabs.datatoolbackend.entity.TradePiece;
import com.gmclabs.datatoolbackend.service.TradePieceServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradePieceDatas {
    private final TradePieceServiceImpl tradePieceService;

    private List<TradePieceData> tradePieceData;
    private String fee;
    private TradeName tradeName;

    public void initializeTradePieceData() {
        this.tradePieceData = new ArrayList<>();
    }

    public void set(String fee, TradeName tradeName) {
        this.fee = fee;
        this.tradeName = tradeName;
    }

    public void addTradePieceData(TradePieceData tradePieceData) {
        this.tradePieceData.add(tradePieceData);
    }

    public void calculateTradePiece() {
        BuyProcess buyProcess = new BuyProcess();
        tradePieceService.setTradePieces();

        TradePiece tradePiece = new TradePiece();

        for (int i = 1; i < tradePieceData.size(); i++) {
            if (buyProcess.isBuyProcess()) {
                tradePieceData.get(i).sumBuyData(tradePiece, tradeName);
                buyProcess.turnBuyProcess();
                continue;
            }

            if (tradePieceData.get(i).isSaveProcess()) {
                tradePieceData.get(i).sumSellData(tradePiece, fee);
                tradePieceService.addTradePieces(tradePiece);
                buyProcess.turnSellProcess();
                tradePiece = new TradePiece();
            }
        }
    }

    public void saveTradePieceData() {
        tradePieceService.saveData();
    }

}
