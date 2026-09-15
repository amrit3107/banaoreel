package com.banaoreel.backend.repository;

import com.banaoreel.backend.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, UUID> {
    List<WalletTransaction> findByWalletIdOrderByCreatedAtDesc(UUID walletId);
}
