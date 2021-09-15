package com.gmclabs.datatoolbackend.domain;

import org.springframework.stereotype.Service;

@Service
public class BuyProcess {
    private int buyIndex;

    public BuyProcess() {
        this.buyIndex = 0;
    }

    public void turnBuyProcess() {
        this.buyIndex++;
    }

    public void turnSellProcess() {
        this.buyIndex = 0;
    }

    public boolean isBuyProcess() {
        if (buyIndex == 0) {
            return true;
        }
        return false;
    }
}
