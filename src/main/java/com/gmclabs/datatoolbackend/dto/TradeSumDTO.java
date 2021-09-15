package com.gmclabs.datatoolbackend.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TradeSumDTO {
    private String revenueTotal; //순수익
    private String revenuePer; //수익률
    private String lossPer; //손실률
}
