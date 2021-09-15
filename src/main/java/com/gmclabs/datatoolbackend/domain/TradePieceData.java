package com.gmclabs.datatoolbackend.domain;

import com.gmclabs.datatoolbackend.entity.TradeName;
import com.gmclabs.datatoolbackend.entity.TradePiece;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TradePieceData {
    private List<String> tradePieceTest;

    public TradePieceData() {
    }

    public TradePieceData(String[] data) {
        tradePieceTest = new ArrayList<>(Arrays.asList(data));
    }

    public void sumBuyData(TradePiece tradePiece, TradeName tradeName) {
        tradePiece.setTradeName(tradeName);
        tradePiece.setCreateAt(tradeName.getCreateAt());
        tradePiece.setUpdatedAt(tradeName.getUpdatedAt());
        tradePiece.setTdId(tradePieceTest.get(0));
        tradePiece.setEntry(tradePieceTest.get(2));
        tradePiece.setStart(tradePieceTest.get(3));
    }

    public boolean isSaveProcess() {
        if (tradePieceTest.size() > 2) {
            return true;
        }
        return false;
    }

    public void sumSellData(TradePiece tradePiece, String fee) {
        tradePiece.setEnd(tradePieceTest.get(3));
        tradePiece.setProfitPer(Calculator.subtractFee(Calculator.calculatePercent(tradePieceTest.get(7)), fee));
        tradePiece.setRunupPer(Calculator.calculatePercent(tradePieceTest.get(11)));
        tradePiece.setDrawdownPer(Calculator.calculatePercent(tradePieceTest.get(13)));
    }
}
