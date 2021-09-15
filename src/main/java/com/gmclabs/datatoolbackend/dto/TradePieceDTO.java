package com.gmclabs.datatoolbackend.dto;

import com.gmclabs.datatoolbackend.entity.TradeName;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class TradePieceDTO {

    private int id;

    private TradeName tradeName;

    private String tdId;

    private String start;

    private String entry;

    private String end;

    private String profitPer;

    private String runupPer;

    private String drawdownPer;

    private Date createAt;

    private Date updatedAt;

    private String memo;
}
