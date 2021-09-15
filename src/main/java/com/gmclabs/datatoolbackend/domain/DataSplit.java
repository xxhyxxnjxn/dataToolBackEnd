package com.gmclabs.datatoolbackend.domain;

import org.springframework.stereotype.Service;

@Service
public class DataSplit {
    public String makeLastResultData(String data) {
        String[] result = data.split("\n\n");
        return result[result.length - 1];
    }

    public void makeElementResultData(String lastResultData, TradePieceDatas tradePieceDatas) {
        String[] lastResultDatas = lastResultData.split("\n");

        tradePieceDatas.initializeTradePieceData();
        for (String row : lastResultDatas) {
            tradePieceDatas.addTradePieceData(new TradePieceData(row.split("\t")));
        }
    }
}
