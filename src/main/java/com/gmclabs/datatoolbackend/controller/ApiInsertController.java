package com.gmclabs.datatoolbackend.controller;

import com.gmclabs.datatoolbackend.domain.*;
import com.gmclabs.datatoolbackend.entity.TradeName;
import com.gmclabs.datatoolbackend.service.TradeNameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@RestController
@RequestMapping("/api/insert")
@RequiredArgsConstructor
public class ApiInsertController {

    private final DataSplit dataSplit;
    private final TradeNameService tradeNameService;
    private final TradePieceDatas tradePieceDatas;
    private final TradeSumLogic tradeSumLogic;
    private final TradeTotalLogic tradeTotalLogic;
    private final Validation validation;

    @ResponseBody
    @PostMapping(value = "/")
    public void test(@RequestParam HashMap<String, String> data) {
        data.put("tdName",validation.invalidateBlank(data.get("tdName")));
        tradeNameService.saveData(data.get("tdName"));
        TradeName tradeName = tradeNameService.selectTitle(data.get("tdName"));

        doDataSplit(data);
        doTradePieceLogic(data, tradeName);
        doTradeSumLogic(tradeName);
        doTradeTotalLogic(tradeName);
    }

    private void doDataSplit(HashMap<String, String> test) {
        String lastResultData = dataSplit.makeLastResultData(test.get("inputData"));
        dataSplit.makeElementResultData(lastResultData, tradePieceDatas);
    }

    private void doTradePieceLogic(HashMap<String, String> test, TradeName tradeName) {
        tradePieceDatas.set(test.get("fee"), tradeName);
        tradePieceDatas.calculateTradePiece();
        tradePieceDatas.saveTradePieceData();
    }

    private void doTradeSumLogic(TradeName tradeName) {
        tradeSumLogic.getGroupByEntry(tradeName);
        tradeSumLogic.saveTradeSumData();
    }

    private void doTradeTotalLogic(TradeName tradeName) {
        tradeTotalLogic.setTradeName(tradeName);
        tradeTotalLogic.calculateTradeTotal();
        tradeTotalLogic.makeTradeTotal();
        tradeTotalLogic.saveTradeTotalData();
    }

}
