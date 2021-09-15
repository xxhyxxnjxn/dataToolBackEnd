package com.gmclabs.datatoolbackend.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmclabs.datatoolbackend.dto.TradePieceCompareData;
import com.gmclabs.datatoolbackend.dto.TradePieceCompareDatas;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class TradePieceCompare {

    @SneakyThrows
    public TradePieceCompareDatas compareTradePiece(HashMap<String, String> data) {

        ObjectMapper mapper = new ObjectMapper();
        JsonNode firstTradePieceArray = mapper.readTree(data.get("first"));
        JsonNode secondTradePieceArray = mapper.readTree(data.get("second"));

        int secondArrayCount = 0;
        int lastSecondData = 0;

        List<TradePieceCompareData> firstTradePieceCompareDatas = new ArrayList<>();
        List<TradePieceCompareData> secondTradePieceCompareDatas = new ArrayList<>();

        for (int i = 0; i < firstTradePieceArray.size(); i++) {
            TradePieceCompareData firstTradePieceCompareData = null;
            TradePieceCompareData secondTradePieceCompareData = null;

            JsonNode firstTradePieceObject = firstTradePieceArray.get(i);
            LocalDateTime firstTradePieceStartDateParse = LocalDateTime.parse(firstTradePieceObject.get("start").asText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            JsonNode secondTradePieceObject = secondTradePieceArray.get(secondArrayCount);
            LocalDateTime secondTradePieceStartDateParse = LocalDateTime.parse(secondTradePieceObject.get("start").asText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            if (firstTradePieceStartDateParse.isBefore(secondTradePieceStartDateParse)) {
                firstTradePieceCompareData = savaObjectData(firstTradePieceObject);
                secondTradePieceCompareData = saveHyphen();

                firstTradePieceCompareDatas.add(firstTradePieceCompareData);
                secondTradePieceCompareDatas.add(secondTradePieceCompareData);

                if (i == firstTradePieceArray.size() - 1) {
                    int remainingIndex = secondTradePieceArray.size() - secondArrayCount;
                    for (int j = 0; j < remainingIndex; j++) {
                        JsonNode remainingSecondTradePieceData = secondTradePieceArray.get(j + secondArrayCount);
                        firstTradePieceCompareData = saveHyphen();
                        secondTradePieceCompareData = savaObjectData(remainingSecondTradePieceData);

                        firstTradePieceCompareDatas.add(firstTradePieceCompareData);
                        secondTradePieceCompareDatas.add(secondTradePieceCompareData);
                    }
                }
            } else if (firstTradePieceStartDateParse.isAfter(secondTradePieceStartDateParse)) {
                if (secondTradePieceArray.size() > secondArrayCount + 1) {
                    firstTradePieceCompareData = saveHyphen();
                    secondTradePieceCompareData = savaObjectData(secondTradePieceObject);
                    secondArrayCount++;
                    i--;
                } else if (secondTradePieceArray.size() == secondArrayCount + 1) {
                    lastSecondData++;
                }

                if (lastSecondData == 1) {
                    firstTradePieceCompareData = saveHyphen();
                    secondTradePieceCompareData = savaObjectData(secondTradePieceObject);
                    i--;
                } else if (lastSecondData > 1) {
                    firstTradePieceCompareData = savaObjectData(firstTradePieceObject);
                    secondTradePieceCompareData = saveHyphen();

                }
                firstTradePieceCompareDatas.add(firstTradePieceCompareData);
                secondTradePieceCompareDatas.add(secondTradePieceCompareData);

            }else if(firstTradePieceStartDateParse.isEqual(secondTradePieceStartDateParse)) {
                if (secondTradePieceArray.size() > secondArrayCount + 1) {
                    firstTradePieceCompareData = savaObjectData(firstTradePieceObject);
                    secondTradePieceCompareData = savaObjectData(secondTradePieceObject);
                    secondArrayCount++;
                }else if (secondTradePieceArray.size() == secondArrayCount + 1) {
                    lastSecondData++;
                }

                if (lastSecondData == 1) {
                    firstTradePieceCompareData = savaObjectData(firstTradePieceObject);
                    secondTradePieceCompareData = savaObjectData(secondTradePieceObject);
                }
                firstTradePieceCompareDatas.add(firstTradePieceCompareData);
                secondTradePieceCompareDatas.add(secondTradePieceCompareData);
            }
        }

        return TradePieceCompareDatas.builder()
                .firstCompareTable(firstTradePieceCompareDatas)
                .secondCompareTable(secondTradePieceCompareDatas)
                .build();
    }

    private TradePieceCompareData savaObjectData(JsonNode object){
        return TradePieceCompareData.builder()
                .start(object.get("start").asText())
                .end(object.get("end").asText())
                .entry(object.get("entry").asText())
                .profitPer(object.get("profitPer").asText())
                .runupPer(object.get("runupPer").asText())
                .drawdownPer(object.get("drawdownPer").asText())
                .build();
    }

    private TradePieceCompareData saveHyphen(){
        return TradePieceCompareData.builder()
                .start("-")
                .end("-")
                .entry("-")
                .profitPer("-")
                .runupPer("-")
                .drawdownPer("-")
                .build();
    }
}
