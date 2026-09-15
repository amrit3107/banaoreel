package com.banaoreel.backend.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "wallet_transactions")
public class WalletTransaction {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID walletId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletTransactionType type;

    @Column(nullable = false)
    private long amountPaise;

    private UUID refJobId;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public UUID getId() { return id; }
    public UUID getWalletId() { return walletId; }
    public void setWalletId(UUID walletId) { this.walletId = walletId; }
    public WalletTransactionType getType() { return type; }
    public void setType(WalletTransactionType type) { this.type = type; }
    public long getAmountPaise() { return amountPaise; }
    public void setAmountPaise(long amountPaise) { this.amountPaise = amountPaise; }
    public UUID getRefJobId() { return refJobId; }
    public void setRefJobId(UUID refJobId) { this.refJobId = refJobId; }
    public Instant getCreatedAt() { return createdAt; }
}
