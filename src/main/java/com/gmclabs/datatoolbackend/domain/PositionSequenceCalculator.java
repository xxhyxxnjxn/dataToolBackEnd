package com.gmclabs.datatoolbackend.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmclabs.datatoolbackend.dto.TradePieceDTO;
import com.gmclabs.datatoolbackend.dto.TradeSumDTO;
import com.gmclabs.datatoolbackend.dto.TradeSumDTO2;
import com.gmclabs.datatoolbackend.dto.TradeTableMaxDataPosition;
import com.gmclabs.datatoolbackend.entity.TradeSum;
import com.gmclabs.datatoolbackend.entity.TradeTotal;
import com.gmclabs.datatoolbackend.service.TradePieceService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PositionSequenceCalculator {

    private final TradePieceService tradePieceService;

    private String tdNameId = null;
    private List<BigDecimal> sequence = null;
    private List<TradePieceDTO> tradePiecesDTO = null;

    @SneakyThrows
    public void makeSequence(HashMap<String, String> data) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode tradeNameObject = mapper.readTree(data.get("tradeName"));

        this.sequence = new ArrayList<>();
        BigDecimal endNumber = new BigDecimal(data.get("maxValue"));
        BigDecimal interval = new BigDecimal(data.get("intervalValue"));
        BigDecimal sequenceNumber = new BigDecimal(data.get("minValue"));

        if(sequenceNumber.compareTo(BigDecimal.ZERO) > 0){
            sequence.add(BigDecimal.ZERO);
        }
        sequence.add(sequenceNumber);
        for (; ; ) {
            if (sequenceNumber.compareTo(endNumber) >= 0) {
                break;
            }
            sequenceNumber = sequenceNumber.add(interval);
            sequence.add(sequenceNumber);
        }
        this.tdNameId = tradeNameObject.get("id").asText();
    }

    public TradeTableMaxDataPosition cycleSequence() {
        this.tradePiecesDTO = tradePieceService.findByTradePieceData(Integer.parseInt(tdNameId));

        List<String> groupByEntries = tradePieceService.getGroupByEntry(tdNameId);
        List<TradeSumDTO2> tradeSumLongsStopProfit = new ArrayList<>();
        List<TradeSumDTO2> tradeSumShortsStopProfit = new ArrayList<>();
        List<TradeSumDTO2> tradeSumLongsStopLoss = new ArrayList<>();
        List<TradeSumDTO2> tradeSumShortsStopLoss = new ArrayList<>();

        List<TradeSumDTO2> lossPerTradeSumLongs = new ArrayList<>();
        List<TradeSumDTO2> revenuePerTradeSumLongs = new ArrayList<>();
        List<TradeSumDTO2> victoryPerTradeSumLongs = new ArrayList<>();

        List<TradeSumDTO2> lossPerTradeSumShorts = new ArrayList<>();
        List<TradeSumDTO2> revenuePerTradeSumShorts = new ArrayList<>();
        List<TradeSumDTO2> victoryPerTradeSumShorts = new ArrayList<>();

        for (String groupByEntry : groupByEntries) {
            for (BigDecimal bigDecimal : sequence) {
                List<TradePieceDTO> tradePiecesStopProfit = calculateTradePiecesStopProfit(bigDecimal);
                List<TradePieceDTO> tradePiecesStopLoss = calculateTradePiecesStopLoss(bigDecimal);

                TradeSumDTO2 tradeSumStopProfit = makeTradeSum(tradePiecesStopProfit, groupByEntry);
                tradeSumStopProfit.setTradePieces(findTradePiecesGroupByEntry(tradePiecesStopProfit,groupByEntry));

                TradeSumDTO2 tradeSumStopLoss = makeTradeSum(tradePiecesStopLoss, groupByEntry);
                tradeSumStopLoss.setTradePieces(findTradePiecesGroupByEntry(tradePiecesStopLoss,groupByEntry));

                if(bigDecimal.compareTo(BigDecimal.ZERO) == 0){
                    tradeSumStopProfit.setInterval(bigDecimal + "%");
                    tradeSumStopLoss.setInterval(bigDecimal + "%");
                }else{
                    tradeSumStopProfit.setInterval("익절 : " + bigDecimal + "%");
                    tradeSumStopLoss.setInterval("손절 : -" + bigDecimal + "%");
                }

                tradeSumStopProfit.setEntry(groupByEntry);
                tradeSumStopLoss.setEntry(groupByEntry);
                if (groupByEntry.contains("long")) {
                    tradeSumLongsStopProfit.add(tradeSumStopProfit);
                    tradeSumLongsStopLoss.add(tradeSumStopLoss);

                } else if (groupByEntry.contains("short")) {
                    tradeSumShortsStopProfit.add(tradeSumStopProfit);
                    tradeSumShortsStopLoss.add(tradeSumStopLoss);
                }
            }

            if (groupByEntry.contains("long")) {
                TradeSumDTO2 tradeSumMinLossPerStopProfitLong = tradeSumLongsStopProfit.stream().max(Comparator.comparing(tradeSum -> Double.valueOf(tradeSum.getLossPer()))).get();
                TradeSumDTO2 tradeSumMinLossPerStopLossLong = tradeSumLongsStopLoss.stream().max(Comparator.comparing(tradeSum -> Double.valueOf(tradeSum.getLossPer()))).get();
                lossPerTradeSumLongs.add(findMinLossPerTradeSumDTO(tradeSumMinLossPerStopProfitLong, tradeSumMinLossPerStopLossLong));

                TradeSumDTO2 tradeSumMinRevenuePerStopProfitLong = tradeSumLongsStopProfit.stream().max(Comparator.comparing(tradeSum -> Double.valueOf(tradeSum.getRevenueTotal()))).get();
                TradeSumDTO2 tradeSumMinRevenuePerStopLossLong = tradeSumLongsStopLoss.stream().max(Comparator.comparing(tradeSum -> Double.valueOf(tradeSum.getRevenuePer()))).get();
                revenuePerTradeSumLongs.add(findMaxRevenuePerTradeSumDTO(tradeSumMinRevenuePerStopProfitLong, tradeSumMinRevenuePerStopLossLong));

                TradeSumDTO2 tradeSumMinVictoryPerStopProfitLong = tradeSumLongsStopProfit.stream().max(Comparator.comparing(tradeSum -> Double.valueOf(tradeSum.getVictoryPer()))).get();
                TradeSumDTO2 tradeSumMinVictoryPerStopLossLong = tradeSumLongsStopLoss.stream().max(Comparator.comparing(tradeSum -> Double.valueOf(tradeSum.getVictoryPer()))).get();
                victoryPerTradeSumLongs.add(findMaxVictoryPerTradeSumDTO(tradeSumMinVictoryPerStopProfitLong, tradeSumMinVictoryPerStopLossLong));
            }else if (groupByEntry.contains("short")) {
                TradeSumDTO2 tradeSumMinLossPerStopProfitShort = tradeSumShortsStopProfit.stream().max(Comparator.comparing(tradeSum -> Double.valueOf(tradeSum.getLossPer()))).get();
                TradeSumDTO2 tradeSumMinLossPerStopLossShort = tradeSumShortsStopLoss.stream().max(Comparator.comparing(tradeSum -> Double.valueOf(tradeSum.getLossPer()))).get();
                lossPerTradeSumShorts.add(findMinLossPerTradeSumDTO(tradeSumMinLossPerStopProfitShort, tradeSumMinLossPerStopLossShort));

                TradeSumDTO2 tradeSumMinRevenuePerStopProfitShort = tradeSumShortsStopProfit.stream().max(Comparator.comparing(tradeSum -> Double.valueOf(tradeSum.getRevenueTotal()))).get();
                TradeSumDTO2 tradeSumMinRevenuePerStopLossShort = tradeSumShortsStopLoss.stream().max(Comparator.comparing(tradeSum -> Double.valueOf(tradeSum.getRevenuePer()))).get();
                revenuePerTradeSumShorts.add(findMaxRevenuePerTradeSumDTO(tradeSumMinRevenuePerStopProfitShort, tradeSumMinRevenuePerStopLossShort));

                TradeSumDTO2 tradeSumMinVictoryPerStopProfitShort = tradeSumShortsStopProfit.stream().max(Comparator.comparing(tradeSum -> Double.valueOf(tradeSum.getVictoryPer()))).get();
                TradeSumDTO2 tradeSumMinVictoryPerStopLossShort = tradeSumShortsStopLoss.stream().max(Comparator.comparing(tradeSum -> Double.valueOf(tradeSum.getVictoryPer()))).get();
                victoryPerTradeSumShorts.add(findMaxVictoryPerTradeSumDTO(tradeSumMinVictoryPerStopProfitShort, tradeSumMinVictoryPerStopLossShort));
            }

            tradeSumLongsStopProfit = new ArrayList<>();
            tradeSumShortsStopProfit = new ArrayList<>();
            tradeSumLongsStopLoss = new ArrayList<>();
            tradeSumShortsStopLoss = new ArrayList<>();
        }

        TradeTotal lossPerLong = makeTradeTotal(lossPerTradeSumLongs,"long");
        TradeTotal lossPerShort = makeTradeTotal(lossPerTradeSumShorts,"short");
        TradeTableDatasPosition tradeTableDatasLossPer = new TradeTableDatasPosition();
        tradeTableDatasLossPer.setTradeTotal(makeTradeTotal(lossPerLong,lossPerShort));
        tradeTableDatasLossPer.setTradeTotalLong(lossPerLong);
        tradeTableDatasLossPer.setTradeTotalShort(lossPerShort);
        tradeTableDatasLossPer.setTradeSumLongs(lossPerTradeSumLongs);
        tradeTableDatasLossPer.setTradeSumShorts(lossPerTradeSumShorts);

        TradeTotal revenuePerLong = makeTradeTotal(revenuePerTradeSumLongs,"long");
        TradeTotal revenuePerShort = makeTradeTotal(revenuePerTradeSumShorts,"short");
        TradeTableDatasPosition tradeTableDatasRevenuePer = new TradeTableDatasPosition();
        tradeTableDatasRevenuePer.setTradeTotal(makeTradeTotal(revenuePerLong,revenuePerShort));
        tradeTableDatasRevenuePer.setTradeTotalLong(revenuePerLong);
        tradeTableDatasRevenuePer.setTradeTotalShort(revenuePerShort);
        tradeTableDatasRevenuePer.setTradeSumLongs(revenuePerTradeSumLongs);
        tradeTableDatasRevenuePer.setTradeSumShorts(revenuePerTradeSumShorts);

        TradeTotal victoryPerLong = makeTradeTotal(victoryPerTradeSumLongs,"long");
        TradeTotal victoryPerShort = makeTradeTotal(victoryPerTradeSumShorts,"short");
        TradeTableDatasPosition tradeTableDatasVictoryPer = new TradeTableDatasPosition();
        tradeTableDatasVictoryPer.setTradeTotal(makeTradeTotal(victoryPerLong,victoryPerShort));
        tradeTableDatasVictoryPer.setTradeTotalLong(victoryPerLong);
        tradeTableDatasVictoryPer.setTradeTotalShort(victoryPerShort);
        tradeTableDatasVictoryPer.setTradeSumLongs(victoryPerTradeSumLongs);
        tradeTableDatasVictoryPer.setTradeSumShorts(victoryPerTradeSumShorts);

        return TradeTableMaxDataPosition.builder()
                .tradeTableMaxLossPerData(tradeTableDatasLossPer)
                .tradeTableMaxRevenuePerData(tradeTableDatasRevenuePer)
                .tradeTableMaxVictoryPerData(tradeTableDatasVictoryPer)
                .build();
    }

    public List<TradePieceDTO> calculateTradePiecesStopProfit(BigDecimal interval) {
        List<TradePieceDTO> tradePieceDTOStopProfit = this.tradePiecesDTO.stream().map(this::clone).collect(Collectors.toList());
        if(interval.compareTo(BigDecimal.ZERO) > 0) {
            for (int i = 0; i < tradePiecesDTO.size(); i++) {
                if (new BigDecimal(tradePiecesDTO.get(i).getRunupPer()).compareTo(interval) > 0) {
                    tradePieceDTOStopProfit.get(i).setProfitPer(interval.toEngineeringString());
                }
            }
        }
        return tradePieceDTOStopProfit;
    }

    public List<TradePieceDTO> calculateTradePiecesStopLoss(BigDecimal interval) {
        List<TradePieceDTO> tradePieceDTOStopLoss = this.tradePiecesDTO.stream().map(this::clone).collect(Collectors.toList());
        if(interval.compareTo(BigDecimal.ZERO) > 0) {
            for (int i = 0; i < tradePiecesDTO.size(); i++) {
                BigDecimal drawDownNegative = new BigDecimal(tradePiecesDTO.get(i).getDrawdownPer());
                BigDecimal intervalNegative = interval.multiply(new BigDecimal("-1"));
                if (drawDownNegative.compareTo(intervalNegative) < 0) {
                    tradePieceDTOStopLoss.get(i).setProfitPer("-" + interval.toEngineeringString());
                }
            }
        }
        return tradePieceDTOStopLoss;
    }

    private TradePieceDTO clone(TradePieceDTO tradePieceDTO) {
        return TradePieceDTO.builder()
                .id(tradePieceDTO.getId())
                .tradeName(tradePieceDTO.getTradeName())
                .tdId(tradePieceDTO.getTdId())
                .start(tradePieceDTO.getStart())
                .entry(tradePieceDTO.getEntry())
                .end(tradePieceDTO.getEnd())
                .profitPer(tradePieceDTO.getProfitPer())
                .runupPer(tradePieceDTO.getRunupPer())
                .drawdownPer(tradePieceDTO.getDrawdownPer())
                .createAt(tradePieceDTO.getCreateAt())
                .updatedAt(tradePieceDTO.getUpdatedAt())
                .memo(tradePieceDTO.getMemo())
                .build();
    }

    private TradeSumDTO2 makeTradeSum(List<TradePieceDTO> tradePiecesDTOs, String entry) {
        BigDecimal revenuePer = BigDecimal.ZERO;
        BigDecimal revenueCount = BigDecimal.ZERO;
        BigDecimal lossPer = BigDecimal.ZERO;
        BigDecimal lossCount = BigDecimal.ZERO;
        BigDecimal revenueTotal;
        BigDecimal victoryPer = BigDecimal.ZERO;

        for (int i = 0; i < tradePiecesDTOs.size(); i++) {
            TradePieceDTO tradePieceDTO = tradePiecesDTOs.get(i);
            if (entry.equals(tradePieceDTO.getEntry())) {
                BigDecimal profitPer = new BigDecimal(tradePieceDTO.getProfitPer());
                if (profitPer.compareTo(BigDecimal.ZERO) > 0) {
                    revenuePer = revenuePer.add(profitPer);
                    revenueCount = revenueCount.add(BigDecimal.ONE);
                } else if (profitPer.compareTo(BigDecimal.ZERO) < 0) {
                    lossPer = lossPer.add(profitPer);
                    lossCount = lossCount.add(BigDecimal.ONE);
                }
            }
        }

        revenueTotal = revenuePer.add(lossPer);

        if (revenueCount.compareTo(BigDecimal.ZERO) > 0) {
            victoryPer = revenueCount.divide(revenueCount.add(lossCount), 8, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100.0));
        }


        return TradeSumDTO2.builder()
                .revenuePer(revenuePer.toEngineeringString())
                .lossPer(lossPer.toEngineeringString())
                .revenueTotal(revenueTotal.toEngineeringString())
                .transCount(revenueCount.add(lossCount).toEngineeringString())
                .revenueCount(String.valueOf(revenueCount))
                .lossCount(String.valueOf(lossCount))
                .victoryPer(victoryPer.toEngineeringString())
                .build();
    }

    private List<TradePieceDTO> findTradePiecesGroupByEntry(List<TradePieceDTO> tradePieces, String entry){
        return tradePieces.stream().filter(tradePieceDTO -> tradePieceDTO.getEntry().equals(entry)).collect(Collectors.toList());
    }

    private TradeSumDTO2 findMinLossPerTradeSumDTO(TradeSumDTO2 tradeSumMaxStopProfit, TradeSumDTO2 tradeSumMaxStopLoss){
        if(new BigDecimal(tradeSumMaxStopProfit.getLossPer()).compareTo(new BigDecimal(tradeSumMaxStopLoss.getLossPer())) > 0 ){
            return tradeSumMaxStopProfit;
        }else if(new BigDecimal(tradeSumMaxStopProfit.getLossPer()).compareTo(new BigDecimal(tradeSumMaxStopLoss.getLossPer())) == 0){
            if(tradeSumMaxStopProfit.getInterval().contains("0%")){
                return tradeSumMaxStopProfit;
            }else if(tradeSumMaxStopLoss.getInterval().contains("0%")){
                return tradeSumMaxStopLoss;
            }else if(tradeSumMaxStopProfit.getInterval().contains("익절")){
                return tradeSumMaxStopProfit;
            }
        }
        return tradeSumMaxStopLoss;
    }

    private TradeSumDTO2 findMaxRevenuePerTradeSumDTO(TradeSumDTO2 tradeSumMaxStopProfit, TradeSumDTO2 tradeSumMaxStopLoss){
        if(new BigDecimal(tradeSumMaxStopProfit.getRevenuePer()).compareTo(new BigDecimal(tradeSumMaxStopLoss.getRevenuePer())) > 0 ){
            return tradeSumMaxStopProfit;
        }else if(new BigDecimal(tradeSumMaxStopProfit.getLossPer()).compareTo(new BigDecimal(tradeSumMaxStopLoss.getLossPer())) == 0){
            if(tradeSumMaxStopProfit.getInterval().contains("0%")){
                return tradeSumMaxStopProfit;
            }else if(tradeSumMaxStopLoss.getInterval().contains("0%")){
                return tradeSumMaxStopLoss;
            }else if(tradeSumMaxStopProfit.getInterval().contains("익절")){
                return tradeSumMaxStopProfit;
            }
        }
        return tradeSumMaxStopLoss;
    }

    private TradeSumDTO2 findMaxVictoryPerTradeSumDTO(TradeSumDTO2 tradeSumMaxStopProfit, TradeSumDTO2 tradeSumMaxStopLoss){
        if(new BigDecimal(tradeSumMaxStopProfit.getVictoryPer()).compareTo(new BigDecimal(tradeSumMaxStopLoss.getVictoryPer())) > 0 ){
            return tradeSumMaxStopProfit;
        }else if(new BigDecimal(tradeSumMaxStopProfit.getLossPer()).compareTo(new BigDecimal(tradeSumMaxStopLoss.getLossPer())) == 0){
            if(tradeSumMaxStopProfit.getInterval().contains("0%")){
                return tradeSumMaxStopProfit;
            }else if(tradeSumMaxStopLoss.getInterval().contains("0%")){
                return tradeSumMaxStopLoss;
            }else if(tradeSumMaxStopProfit.getInterval().contains("익절")){
                return tradeSumMaxStopProfit;
            }
        }
        return tradeSumMaxStopLoss;
    }

    private TradeTotal makeTradeTotal(List<TradeSumDTO2> tradeSums, String category){
        TradeTotal tradeTotal = new TradeTotal();

        BigDecimal revenuePer = BigDecimal.ZERO;
        BigDecimal lossPer = BigDecimal.ZERO;
        BigDecimal revenueTotal = BigDecimal.ZERO;
        BigDecimal transCount = BigDecimal.ZERO;
        BigDecimal revenueCount = BigDecimal.ZERO;
        BigDecimal lossCount = BigDecimal.ZERO;

        for(int i=0;i<tradeSums.size();i++){
            revenuePer = revenuePer.add(new BigDecimal(tradeSums.get(i).getRevenuePer()));
            lossPer = lossPer.add(new BigDecimal(tradeSums.get(i).getLossPer()));
            revenueTotal = revenueTotal.add(new BigDecimal(tradeSums.get(i).getRevenueTotal()));
            transCount = transCount.add(new BigDecimal(tradeSums.get(i).getTransCount()));
            revenueCount = revenueCount.add(new BigDecimal(tradeSums.get(i).getRevenueCount()));
            lossCount = lossCount.add(new BigDecimal(tradeSums.get(i).getLossCount()));
        }

        tradeTotal.setCategory(category);
        tradeTotal.setRevenuePer(revenuePer.toEngineeringString());
        tradeTotal.setLossPer(lossPer.toEngineeringString());
        tradeTotal.setRevenueTotal(revenueTotal.toEngineeringString());
        tradeTotal.setTransCount(transCount.toEngineeringString());
        tradeTotal.setRevenueCount(revenueCount.toEngineeringString());
        tradeTotal.setLossCount(lossCount.toEngineeringString());
        if(tradeSums.size() > 0){
            tradeTotal.setVictoryPer(revenueCount.divide(revenueCount.add(lossCount), 8, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100.0)).toEngineeringString());
        }else{
            tradeTotal.setVictoryPer("0");
        }

        return tradeTotal;
    }

    private TradeTotal makeTradeTotal(TradeTotal totalLong, TradeTotal totalShort){

        BigDecimal transCount = new BigDecimal(totalLong.getTransCount()).add(new BigDecimal(totalShort.getTransCount()));
        BigDecimal revenueCount = new BigDecimal(totalLong.getRevenueCount()).add(new BigDecimal(totalShort.getRevenueCount()));
        BigDecimal lossCount = new BigDecimal(totalLong.getLossCount()).add(new BigDecimal(totalShort.getLossCount()));
        BigDecimal victoryPer = revenueCount.divide(transCount, 8, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100.0));

        TradeTotal tradeTotal = new TradeTotal();
        tradeTotal.setCategory("total");
        tradeTotal.setRevenuePer(new BigDecimal(totalLong.getRevenuePer()).add(new BigDecimal(totalShort.getRevenuePer())).toEngineeringString());
        tradeTotal.setLossPer(new BigDecimal(totalLong.getLossPer()).add(new BigDecimal(totalShort.getLossPer())).toEngineeringString());
        tradeTotal.setRevenueTotal(new BigDecimal(totalLong.getRevenueTotal()).add(new BigDecimal(totalShort.getRevenueTotal())).toEngineeringString());
        tradeTotal.setTransCount(transCount.toEngineeringString());
        tradeTotal.setRevenueCount(revenueCount.toEngineeringString());
        tradeTotal.setLossCount(lossCount.toEngineeringString());
        tradeTotal.setVictoryPer(victoryPer.toEngineeringString());
        return tradeTotal;
    }
}
