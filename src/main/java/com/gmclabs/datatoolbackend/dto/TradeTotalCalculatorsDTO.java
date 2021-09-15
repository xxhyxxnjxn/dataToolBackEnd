package com.gmclabs.datatoolbackend.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TradeTotalCalculatorsDTO {
    TradeTotalCalculatorDTO entityTotal;
    TradeTotalCalculatorDTO entityOther;
}
