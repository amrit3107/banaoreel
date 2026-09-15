package com.banaoreel.backend.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "wallets")
public class Wallet {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID userId;

    // Stored as integer paise — never use float/double for money.
    @Column(nullable = false)
    private long balancePaise = 0;

    // Optimistic lock to protect against concurrent debit/recharge races.
    @Version
    private Long version;

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public long getBalancePaise() { return balancePaise; }
    public void setBalancePaise(long balancePaise) { this.balancePaise = balancePaise; }
}
