package com.gmclabs.datatoolbackend.controller;

import com.gmclabs.datatoolbackend.api.HttpClient;
import com.gmclabs.datatoolbackend.domain.PositionSequenceCalculator;
import com.gmclabs.datatoolbackend.domain.TotalSequenceCalculator;
import com.gmclabs.datatoolbackend.domain.TradePieceCompare;
import com.gmclabs.datatoolbackend.domain.TradeTableDatas;
import com.gmclabs.datatoolbackend.dto.TradePieceCompareDatas;
import com.gmclabs.datatoolbackend.dto.TradeTableMaxData;
import com.gmclabs.datatoolbackend.dto.TradeTableMaxDataPosition;
import com.gmclabs.datatoolbackend.entity.TradeName;
import com.gmclabs.datatoolbackend.service.TradeNameService;
import com.gmclabs.datatoolbackend.service.TradePieceServiceImpl;
import com.gmclabs.datatoolbackend.service.TradeSumService;
import com.gmclabs.datatoolbackend.service.TradeTotalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/show")
@RequiredArgsConstructor
public class ApiShowController {

    private final TradeNameService tradeNameService;
    private final TradeTotalService tradeTotalService;
    private final TradeSumService tradeSumService;
    private final TradePieceServiceImpl tradePieceService;
    private final TradePieceCompare tradePieceCompare;
    private final TotalSequenceCalculator totalSequenceCalculator;
    private final PositionSequenceCalculator positionSequenceCalculator;
    private final HttpClient httpClient;

    @ResponseBody
    @PostMapping(value = "/")
    public List<TradeName> showTradeNames() {
        return tradeNameService.findAll();
    }

    @ResponseBody
    @PostMapping(value = "/tradeTotal")
    public TradeTableDatas showTradeTotal(@RequestParam HashMap<String, String> data) {
        TradeTableDatas tradeTableDatas = new TradeTableDatas();
        tradeTableDatas.setTradeTotal(tradeTotalService.findByCategoryTotal("total", Integer.parseInt(data.get("tdNameId"))));
        tradeTableDatas.setTradeTotalLong(tradeTotalService.findByCategoryTotal("long", Integer.parseInt(data.get("tdNameId"))));
        tradeTableDatas.setTradeTotalShort(tradeTotalService.findByCategoryTotal("short", Integer.parseInt(data.get("tdNameId"))));
        tradeTableDatas.setTradeSumShorts(tradeSumService.findEntryLikeShort(Integer.parseInt(data.get("tdNameId"))));
        tradeTableDatas.setTradeSumLongs(tradeSumService.findEntryLikeLong(Integer.parseInt(data.get("tdNameId"))));
        tradeTableDatas.setTradePieces(tradePieceService.findByTradePieceData(Integer.parseInt(data.get("tdNameId"))));

        return tradeTableDatas;
    }

    @ResponseBody
    @PostMapping(value = "/compareData")
    public TradePieceCompareDatas showCompareData(@RequestParam HashMap<String, String> data) {

        return tradePieceCompare.compareTradePiece(data);
    }

    @ResponseBody
    @PostMapping(value = "/calculate/test/position")
    public TradeTableMaxDataPosition calculateTestPosition(@RequestParam HashMap<String, String> data) {
        positionSequenceCalculator.makeSequence(data);
        return positionSequenceCalculator.cycleSequence();
    }

    @ResponseBody
    @PostMapping(value = "/calculate/test/total")
    public TradeTableMaxData calculateTestTotal(@RequestParam HashMap<String, String> data) {
        totalSequenceCalculator.makeSequence(data);
        return totalSequenceCalculator.cycleSequence();
    }

    @ResponseBody
    @GetMapping(value = "/rate")
    public String showTradingView(){
        return httpClient.get();
    }
}
