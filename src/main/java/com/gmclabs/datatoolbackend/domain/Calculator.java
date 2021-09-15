package com.gmclabs.datatoolbackend.domain;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class Calculator {

    public static String calculatePercent(String percentValue) {
        BigDecimal result = new BigDecimal(percentValue).multiply(BigDecimal.valueOf(100.0));

        return result.toEngineeringString();
    }

    public static String subtractFee(String percentValue, String fee) {
        BigDecimal result = new BigDecimal(percentValue).subtract(new BigDecimal(fee));

        return result.toEngineeringString();
    }
}
