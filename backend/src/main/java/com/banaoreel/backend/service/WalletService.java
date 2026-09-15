package com.banaoreel.backend.service;

import com.banaoreel.backend.entity.Wallet;
import com.banaoreel.backend.entity.WalletTransaction;
import com.banaoreel.backend.entity.WalletTransactionType;
import com.banaoreel.backend.repository.WalletRepository;
import com.banaoreel.backend.repository.WalletTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;

    public WalletService(WalletRepository walletRepository, WalletTransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public Wallet getOrCreateWallet(UUID userId) {
        return walletRepository.findByUserId(userId).orElseGet(() -> {
            Wallet wallet = new Wallet();
            wallet.setUserId(userId);
            wallet.setBalancePaise(0);
            return walletRepository.save(wallet);
        });
    }

    /**
     * Debits the wallet. Relies on JPA optimistic locking (Wallet.version) to
     * catch concurrent debits — callers should retry on OptimisticLockingFailureException.
     * Throws InsufficientBalanceException if the balance is too low; callers
     * must not create a job when this throws.
     */
    @Transactional
    public void debit(UUID userId, long amountPaise, UUID refJobId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Wallet not found for user " + userId));

        if (wallet.getBalancePaise() < amountPaise) {
            throw new InsufficientBalanceException("Insufficient wallet balance");
        }

        wallet.setBalancePaise(wallet.getBalancePaise() - amountPaise);
        walletRepository.save(wallet);

        WalletTransaction txn = new WalletTransaction();
        txn.setWalletId(wallet.getId());
        txn.setType(WalletTransactionType.DEBIT);
        txn.setAmountPaise(amountPaise);
        txn.setRefJobId(refJobId);
        transactionRepository.save(txn);
    }

    @Transactional
    public void refund(UUID userId, long amountPaise, UUID refJobId) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Wallet not found for user " + userId));

        wallet.setBalancePaise(wallet.getBalancePaise() + amountPaise);
        walletRepository.save(wallet);

        WalletTransaction txn = new WalletTransaction();
        txn.setWalletId(wallet.getId());
        txn.setType(WalletTransactionType.REFUND);
        txn.setAmountPaise(amountPaise);
        txn.setRefJobId(refJobId);
        transactionRepository.save(txn);
    }

    @Transactional
    public void credit(UUID userId, long amountPaise) {
        Wallet wallet = getOrCreateWallet(userId);
        wallet.setBalancePaise(wallet.getBalancePaise() + amountPaise);
        walletRepository.save(wallet);

        WalletTransaction txn = new WalletTransaction();
        txn.setWalletId(wallet.getId());
        txn.setType(WalletTransactionType.RECHARGE);
        txn.setAmountPaise(amountPaise);
        transactionRepository.save(txn);
    }
}
