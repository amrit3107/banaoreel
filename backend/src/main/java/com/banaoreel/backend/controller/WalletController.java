package com.banaoreel.backend.controller;

import com.banaoreel.backend.dto.RechargeInitiateRequest;
import com.banaoreel.backend.dto.RechargeVerifyRequest;
import com.banaoreel.backend.entity.Wallet;
import com.banaoreel.backend.entity.WalletTransaction;
import com.banaoreel.backend.repository.WalletTransactionRepository;
import com.banaoreel.backend.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Auth note: userId is read from X-User-Id for now as a scaffold placeholder.
 * TODO: replace with a Spring Security filter that validates the JWT and
 * populates the authenticated principal — never trust a client-supplied header in prod.
 */
@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;
    private final WalletTransactionRepository transactionRepository;

    public WalletController(WalletService walletService, WalletTransactionRepository transactionRepository) {
        this.walletService = walletService;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping
    public Wallet getWallet(@RequestHeader("X-User-Id") UUID userId) {
        return walletService.getOrCreateWallet(userId);
    }

    @GetMapping("/transactions")
    public List<WalletTransaction> getTransactions(@RequestHeader("X-User-Id") UUID userId) {
        Wallet wallet = walletService.getOrCreateWallet(userId);
        return transactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId());
    }

    @PostMapping("/recharge/initiate")
    public Object initiateRecharge(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody RechargeInitiateRequest request
    ) {
        // TODO: create a Razorpay order via razorpay-java client, persist a RechargeOrder
        // row with status "created", and return {gatewayOrderId, amountPaise} to the app.
        throw new UnsupportedOperationException("TODO: wire Razorpay order creation");
    }

    @PostMapping("/recharge/verify")
    public Wallet verifyRecharge(
            @RequestHeader("X-User-Id") UUID userId,
            @Valid @RequestBody RechargeVerifyRequest request
    ) {
        // TODO: verify Razorpay signature (Utils.verifyPaymentSignature), mark the
        // RechargeOrder as "paid", then walletService.credit(userId, order.getAmountPaise()).
        // Also handle this via a Razorpay webhook as a reliability backup, per the plan.
        throw new UnsupportedOperationException("TODO: wire Razorpay signature verification");
    }
}
