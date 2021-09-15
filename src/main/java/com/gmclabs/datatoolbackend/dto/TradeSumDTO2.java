package com.gmclabs.datatoolbackend.dto;

import com.gmclabs.datatoolbackend.entity.TradeName;
import com.gmclabs.datatoolbackend.entity.TradeSum;
import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class TradeSumDTO2 {

    private int id;

    private TradeName tradeName;

    private String revenuePer;

    private String lossPer;

    private String revenueTotal;

    private String transCount;

    private String revenueCount;

    private String lossCount;

    private String victoryPer;

    private Date createAt;

    private Date updatedAt;

    private String entry;

    private String memo;

    List<TradePieceDTO> tradePieces;

    private String interval;
}
