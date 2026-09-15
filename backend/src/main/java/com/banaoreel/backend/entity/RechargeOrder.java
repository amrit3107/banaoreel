package com.banaoreel.backend.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "recharge_orders")
public class RechargeOrder {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private long amountPaise;

    @Column(nullable = false, unique = true)
    private String gatewayOrderId;

    @Column(nullable = false)
    private String status = "created"; // created | paid | failed

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public long getAmountPaise() { return amountPaise; }
    public void setAmountPaise(long amountPaise) { this.amountPaise = amountPaise; }
    public String getGatewayOrderId() { return gatewayOrderId; }
    public void setGatewayOrderId(String gatewayOrderId) { this.gatewayOrderId = gatewayOrderId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
