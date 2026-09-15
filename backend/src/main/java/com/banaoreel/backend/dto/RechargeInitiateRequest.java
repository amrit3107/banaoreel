package com.banaoreel.backend.dto;

import jakarta.validation.constraints.Min;

public class RechargeInitiateRequest {
    @Min(1000) // minimum Rs 10 recharge
    private long amountPaise;

    public long getAmountPaise() { return amountPaise; }
    public void setAmountPaise(long amountPaise) { this.amountPaise = amountPaise; }
}
