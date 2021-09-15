package com.gmclabs.datatoolbackend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TradeTotalCalculatorDTO {
    private String revenueTotal; // 순수익
    private String revenuePer; //수익률
    private String lossPer; // 손실률
}
