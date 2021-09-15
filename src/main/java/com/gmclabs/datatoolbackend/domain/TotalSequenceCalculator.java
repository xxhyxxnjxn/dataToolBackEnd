package com.gmclabs.datatoolbackend.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmclabs.datatoolbackend.dto.TradePieceDTO;
import com.gmclabs.datatoolbackend.dto.TradeTableMaxData;
import com.gmclabs.datatoolbackend.entity.TradeSum;
import com.gmclabs.datatoolbackend.entity.TradeTotal;
import com.gmclabs.datatoolbackend.service.TradePieceService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TotalSequenceCalculator {

    private final TradePieceService tradePieceService;

    private String tdNameId = null;
    private List<BigDecimal> sequence = null;
    private List<TradePieceDTO> tradePiecesDTO = null;
    private List<TradeTableDatas> tradeTableDatasStopProfits = null;
    private List<TradeTableDatas> tradeTableDatasStopLosses = null;

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

    public TradeTableMaxData cycleSequence() {
        this.tradeTableDatasStopProfits = new ArrayList<>();
        this.tradeTableDatasStopLosses = new ArrayList<>();
        this.tradePiecesDTO = tradePieceService.findByTradePieceData(Integer.parseInt(tdNameId));

        List<String> groupByEntries = tradePieceService.getGroupByEntry(tdNameId);
        List<TradeSum> tradeSumLongsStopProfit = new ArrayList<>();
        List<TradeSum> tradeSumShortsStopProfit = new ArrayList<>();
        List<TradeSum> tradeSumLongsStopLoss = new ArrayList<>();
        List<TradeSum> tradeSumShortsStopLoss = new ArrayList<>();

        for (int i = 0; i < sequence.size(); i++) {
            TradeTableDatas tradeTableDatasStopProfit = new TradeTableDatas();
            TradeTableDatas tradeTableDatasStopLoss = new TradeTableDatas();
            tradeTableDatasStopProfit.setTradePieces(calculateTradePiecesStopProfit(sequence.get(i)));
            tradeTableDatasStopLoss.setTradePieces(calculateTradePiecesStopLoss(sequence.get(i)));

            if(sequence.get(i).compareTo(BigDecimal.ZERO) == 0){
                tradeTableDatasStopProfit.setInterval(sequence.get(i)+"%");
                tradeTableDatasStopLoss.setInterval(sequence.get(i)+"%");
            }else{
                tradeTableDatasStopProfit.setInterval("익절 : "+sequence.get(i)+"%");
                tradeTableDatasStopLoss.setInterval("손절 : -"+sequence.get(i)+"%");
            }
            for (int j = 0; j < groupByEntries.size(); j++) {
                TradeSum tradeSumStopProfit = makeTradeSum(tradeTableDatasStopProfit.getTradePieces(),groupByEntries.get(j));
                TradeSum tradeSumStopLoss = makeTradeSum(tradeTableDatasStopLoss.getTradePieces(),groupByEntries.get(j));

//                tradeSum.setTradeName(tradeName);
//                tradeSum.setCreateAt(tradeName.getCreateAt());
//                tradeSum.setUpdatedAt(tradeName.getUpdatedAt());
                tradeSumStopProfit.setEntry(groupByEntries.get(j));
                tradeSumStopLoss.setEntry(groupByEntries.get(j));
                if(groupByEntries.get(j).contains("long")){
                    tradeSumLongsStopProfit.add(tradeSumStopProfit);
                    tradeSumLongsStopLoss.add(tradeSumStopLoss);
                }else if(groupByEntries.get(j).contains("short")){
                    tradeSumShortsStopProfit.add(tradeSumStopProfit);
                    tradeSumShortsStopLoss.add(tradeSumStopLoss);
                }
            }

            tradeTableDatasStopProfit.setTradeSumLongs(tradeSumLongsStopProfit);
            tradeTableDatasStopProfit.setTradeSumShorts(tradeSumShortsStopProfit);
            tradeTableDatasStopLoss.setTradeSumLongs(tradeSumLongsStopLoss);
            tradeTableDatasStopLoss.setTradeSumShorts(tradeSumShortsStopLoss);

            TradeTotal totalLongStopProfit = makeTradeTotal(tradeSumLongsStopProfit,"long");
            TradeTotal totalShortStopProfit = makeTradeTotal(tradeSumShortsStopProfit,"short");
            tradeTableDatasStopProfit.setTradeTotalLong(totalLongStopProfit);
            tradeTableDatasStopProfit.setTradeTotalShort(totalShortStopProfit);
            tradeTableDatasStopProfit.setTradeTotal(makeTradeTotal(totalLongStopProfit,totalShortStopProfit));

            TradeTotal totalLongStopLoss = makeTradeTotal(tradeSumLongsStopLoss,"long");
            TradeTotal totalShortStopLoss = makeTradeTotal(tradeSumShortsStopLoss,"short");
            tradeTableDatasStopLoss.setTradeTotalLong(totalLongStopLoss);
            tradeTableDatasStopLoss.setTradeTotalShort(totalShortStopLoss);
            tradeTableDatasStopLoss.setTradeTotal(makeTradeTotal(totalLongStopLoss,totalShortStopLoss));

            tradeTableDatasStopProfits.add(tradeTableDatasStopProfit);
            tradeTableDatasStopLosses.add(tradeTableDatasStopLoss);

            tradeSumLongsStopProfit = new ArrayList<>();
            tradeSumShortsStopProfit = new ArrayList<>();
            tradeSumLongsStopLoss = new ArrayList<>();
            tradeSumShortsStopLoss = new ArrayList<>();
        }

        TradeTableDatas tradeTableDatasMinLossPerStopProfit = tradeTableDatasStopProfits.stream().max(Comparator.comparing(tradeTableDatas -> Double.valueOf(tradeTableDatas.getTradeTotal().getLossPer()))).get();
        TradeTableDatas tradeTableDatasMaxRevenuePerStopProfit = tradeTableDatasStopProfits.stream().max(Comparator.comparing(tradeTableDatas -> Double.valueOf(tradeTableDatas.getTradeTotal().getRevenueTotal()))).get();
        TradeTableDatas tradeTableDatasMaxVictoryPerStopProfit = tradeTableDatasStopProfits.stream().max(Comparator.comparing(tradeTableDatas -> Double.valueOf(tradeTableDatas.getTradeTotal().getVictoryPer()))).get();

        TradeTableDatas tradeTableDatasMinLossPerStopLoss = tradeTableDatasStopLosses.stream().max(Comparator.comparing(tradeTableDatas -> Double.valueOf(tradeTableDatas.getTradeTotal().getLossPer()))).get();
        TradeTableDatas tradeTableDatasMaxRevenuePerStopLoss = tradeTableDatasStopLosses.stream().max(Comparator.comparing(tradeTableDatas -> Double.valueOf(tradeTableDatas.getTradeTotal().getRevenueTotal()))).get();
        TradeTableDatas tradeTableDatasMaxVictoryPerStopLoss = tradeTableDatasStopLosses.stream().max(Comparator.comparing(tradeTableDatas -> Double.valueOf(tradeTableDatas.getTradeTotal().getVictoryPer()))).get();

        return TradeTableMaxData.builder()
                .tradeTableMaxLossPerData(findMaxLossPerInObject(tradeTableDatasMinLossPerStopProfit,tradeTableDatasMinLossPerStopLoss))
                .tradeTableMaxRevenuePerData(findMaxRevenuePerInObject(tradeTableDatasMaxRevenuePerStopProfit,tradeTableDatasMaxRevenuePerStopLoss))
                .tradeTableMaxVictoryPerData(findMaxVictoryPerInObject(tradeTableDatasMaxVictoryPerStopProfit,tradeTableDatasMaxVictoryPerStopLoss))
                .build();
    }

    public List<TradePieceDTO> calculateTradePiecesStopProfit(BigDecimal interval) {
        List<TradePieceDTO> tradePieceDTOStopProfit = this.tradePiecesDTO.stream().map(this::clone).collect(Collectors.toList());
        if(interval.compareTo(BigDecimal.ZERO) > 0){
            for(int i=0 ;i < tradePiecesDTO.size(); i++){
                if(new BigDecimal(tradePiecesDTO.get(i).getRunupPer()).compareTo(interval) > 0 ) {
                    tradePieceDTOStopProfit.get(i).setProfitPer(interval.toEngineeringString());
                }
            }
        }
        return tradePieceDTOStopProfit;
    }

    public List<TradePieceDTO> calculateTradePiecesStopLoss(BigDecimal interval) {
        List<TradePieceDTO> tradePieceDTOStopLoss = this.tradePiecesDTO.stream().map(this::clone).collect(Collectors.toList());
        if(interval.compareTo(BigDecimal.ZERO) > 0){
            for(int i=0 ;i < tradePiecesDTO.size(); i++){
                BigDecimal drawDownNegative = new BigDecimal(tradePiecesDTO.get(i).getDrawdownPer());
                BigDecimal intervalNegative = interval.multiply(new BigDecimal("-1"));
                if(drawDownNegative.compareTo(intervalNegative) < 0 ) {
                    tradePieceDTOStopLoss.get(i).setProfitPer("-"+interval.toEngineeringString());
                }
            }
        }
        return tradePieceDTOStopLoss;
    }

    private TradeSum makeTradeSum(List<TradePieceDTO> tradePiecesDTOs, String entry) {
        TradeSum tradeSum = new TradeSum();
        BigDecimal revenuePer = BigDecimal.ZERO;
        BigDecimal revenueCount = BigDecimal.ZERO;
        BigDecimal lossPer = BigDecimal.ZERO;
        BigDecimal lossCount = BigDecimal.ZERO;
        BigDecimal revenueTotal;
        BigDecimal victoryPer = BigDecimal.ZERO;

        for (int i = 0; i < tradePiecesDTOs.size(); i++) {
            TradePieceDTO tradePieceDTO = tradePiecesDTOs.get(i);
            if(entry.equals(tradePieceDTO.getEntry())){
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

        tradeSum.setRevenuePer(revenuePer.toEngineeringString());
        tradeSum.setLossPer(lossPer.toEngineeringString());
        tradeSum.setRevenueTotal(revenueTotal.toEngineeringString());
        tradeSum.setTransCount(revenueCount.add(lossCount).toEngineeringString());
        tradeSum.setRevenueCount(String.valueOf(revenueCount));
        tradeSum.setLossCount(String.valueOf(lossCount));
        tradeSum.setVictoryPer(victoryPer.toEngineeringString());
        return tradeSum;
    }

    private TradeTotal makeTradeTotal(List<TradeSum> tradeSums,String category){
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
        tradeTotal.setRevenuePer(new BigDecimal(totalLong.getRevenuePer()).add(new BigDecimal(totalShort.getRevenuePer())).toEngineeringString());
        tradeTotal.setLossPer(new BigDecimal(totalLong.getLossPer()).add(new BigDecimal(totalShort.getLossPer())).toEngineeringString());
        tradeTotal.setRevenueTotal(new BigDecimal(totalLong.getRevenueTotal()).add(new BigDecimal(totalShort.getRevenueTotal())).toEngineeringString());
        tradeTotal.setTransCount(transCount.toEngineeringString());
        tradeTotal.setRevenueCount(revenueCount.toEngineeringString());
        tradeTotal.setLossCount(lossCount.toEngineeringString());
        tradeTotal.setVictoryPer(victoryPer.toEngineeringString());
        return tradeTotal;
    }

    private TradePieceDTO clone(TradePieceDTO tradePieceDTO){
        return tradePieceDTO.builder()
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

    private TradeTableDatas findMaxLossPerInObject(TradeTableDatas tradeTableDatasMinLossPerStopProfit,TradeTableDatas tradeTableDatasMinLossPerStopLoss){
        if(new BigDecimal(tradeTableDatasMinLossPerStopProfit.getTradeTotal().getLossPer()).compareTo(new BigDecimal(tradeTableDatasMinLossPerStopLoss.getTradeTotal().getLossPer())) > 0 ){
            return tradeTableDatasMinLossPerStopProfit;
        }else if(new BigDecimal(tradeTableDatasMinLossPerStopProfit.getTradeTotal().getLossPer()).compareTo(new BigDecimal(tradeTableDatasMinLossPerStopLoss.getTradeTotal().getLossPer())) == 0){
            if(tradeTableDatasMinLossPerStopProfit.getInterval().contains("0%")){
                return tradeTableDatasMinLossPerStopProfit;
            }else if(tradeTableDatasMinLossPerStopLoss.getInterval().contains("0%")){
                return tradeTableDatasMinLossPerStopLoss;
            }else if(tradeTableDatasMinLossPerStopProfit.getInterval().contains("익절")){
                return tradeTableDatasMinLossPerStopProfit;
            }
        }
        return tradeTableDatasMinLossPerStopLoss;
    }

    private TradeTableDatas findMaxVictoryPerInObject(TradeTableDatas tradeTableDatasMaxVictoryPerStopProfit,TradeTableDatas tradeTableDatasMaxVictoryPerStopLoss){
        if(new BigDecimal(tradeTableDatasMaxVictoryPerStopProfit.getTradeTotal().getVictoryPer()).compareTo(new BigDecimal(tradeTableDatasMaxVictoryPerStopLoss.getTradeTotal().getVictoryPer())) > 0 ){
            return tradeTableDatasMaxVictoryPerStopProfit;
        }else if(new BigDecimal(tradeTableDatasMaxVictoryPerStopProfit.getTradeTotal().getLossPer()).compareTo(new BigDecimal(tradeTableDatasMaxVictoryPerStopLoss.getTradeTotal().getLossPer())) == 0){
            if(tradeTableDatasMaxVictoryPerStopProfit.getInterval().contains("0%")){
                return tradeTableDatasMaxVictoryPerStopProfit;
            }else if(tradeTableDatasMaxVictoryPerStopLoss.getInterval().contains("0%")){
                return tradeTableDatasMaxVictoryPerStopLoss;
            }else if(tradeTableDatasMaxVictoryPerStopProfit.getInterval().contains("익절")){
                return tradeTableDatasMaxVictoryPerStopProfit;
            }
        }
        return tradeTableDatasMaxVictoryPerStopLoss;

    }

    private TradeTableDatas findMaxRevenuePerInObject(TradeTableDatas tradeTableDatasMaxRevenuePerStopProfit,TradeTableDatas tradeTableDatasMaxRevenuePerStopLoss){
        if(new BigDecimal(tradeTableDatasMaxRevenuePerStopProfit.getTradeTotal().getRevenuePer()).compareTo(new BigDecimal(tradeTableDatasMaxRevenuePerStopLoss.getTradeTotal().getRevenuePer())) > 0 ){
            return tradeTableDatasMaxRevenuePerStopProfit;
        }else if(new BigDecimal(tradeTableDatasMaxRevenuePerStopProfit.getTradeTotal().getLossPer()).compareTo(new BigDecimal(tradeTableDatasMaxRevenuePerStopLoss.getTradeTotal().getLossPer())) == 0){
            if(tradeTableDatasMaxRevenuePerStopProfit.getInterval().contains("0%")){
                return tradeTableDatasMaxRevenuePerStopProfit;
            }else if(tradeTableDatasMaxRevenuePerStopLoss.getInterval().contains("0%")){
                return tradeTableDatasMaxRevenuePerStopLoss;
            }else if(tradeTableDatasMaxRevenuePerStopProfit.getInterval().contains("익절")){
                return tradeTableDatasMaxRevenuePerStopProfit;
            }
        }
        return tradeTableDatasMaxRevenuePerStopLoss;
    }
}
