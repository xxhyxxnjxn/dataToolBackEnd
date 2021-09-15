package com.gmclabs.datatoolbackend.controller;

import com.gmclabs.datatoolbackend.service.TradeNameService;
import com.gmclabs.datatoolbackend.service.TradePieceServiceImpl;
import com.gmclabs.datatoolbackend.service.TradeSumService;
import com.gmclabs.datatoolbackend.service.TradeTotalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/delete")
@RequiredArgsConstructor
public class ApiDeleteController {
    private final TradeNameService tradeNameService;
    private final TradePieceServiceImpl tradePieceService;
    private final TradeSumService tradeSumService;
    private final TradeTotalService tradeTotalService;

    @ResponseBody
    @PostMapping(value = "/")
    public void deleteData(@RequestParam HashMap<String, String> data) {
        int tdNameId = Integer.parseInt(data.get("tdNameId"));
        tradeNameService.deleteTradeName(tdNameId);
        tradePieceService.deleteTradePiece(tdNameId);
        tradeSumService.deleteTradeSum(tdNameId);
        tradeTotalService.deleteTradeTotal(tdNameId);
    }
}
