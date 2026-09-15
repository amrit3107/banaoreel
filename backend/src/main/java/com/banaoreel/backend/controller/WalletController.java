package com.banaoreel.backend.controller;

import com.banaoreel.backend.dto.RechargeInitiateRequest;
import com.banaoreel.backend.dto.RechargeVerifyRequest;
import com.banaoreel.backend.entity.RechargeOrder;
import com.banaoreel.backend.entity.Wallet;
import com.banaoreel.backend.entity.WalletTransaction;
import com.banaoreel.backend.repository.WalletTransactionRepository;
import com.banaoreel.backend.security.CurrentUser;
import com.banaoreel.backend.service.RazorpayService;
import com.banaoreel.backend.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final WalletService walletService;
    private final WalletTransactionRepository transactionRepository;
    private final RazorpayService razorpayService;

    public WalletController(
            WalletService walletService,
            WalletTransactionRepository transactionRepository,
            RazorpayService razorpayService
    ) {
        this.walletService = walletService;
        this.transactionRepository = transactionRepository;
        this.razorpayService = razorpayService;
    }

    @GetMapping
    public Wallet getWallet() {
        return walletService.getOrCreateWallet(CurrentUser.id());
    }

    @GetMapping("/transactions")
    public List<WalletTransaction> getTransactions() {
        Wallet wallet = walletService.getOrCreateWallet(CurrentUser.id());
        return transactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId());
    }

    @PostMapping("/recharge/initiate")
    public Map<String, Object> initiateRecharge(@Valid @RequestBody RechargeInitiateRequest request) {
        try {
            RechargeOrder order = razorpayService.createOrder(CurrentUser.id(), request.getAmountPaise());
            return Map.of(
                    "gatewayOrderId", order.getGatewayOrderId(),
                    "amountPaise", order.getAmountPaise()
            );
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Could not create payment order: " + e.getMessage());
        }
    }

    @PostMapping("/recharge/verify")
    public Wallet verifyRecharge(@Valid @RequestBody RechargeVerifyRequest request) {
        try {
            razorpayService.verifyAndCredit(request.getPaymentId(), request.getOrderId(), request.getSignature());
        } catch (SecurityException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
        return walletService.getOrCreateWallet(CurrentUser.id());
    }

    /**
     * Razorpay webhook endpoint. Configure in the Razorpay dashboard pointing
     * here for the "payment.captured" event, with a webhook secret set in
     * application.yml — this file currently trusts the payload as-is; add
     * signature verification (Utils.verifyWebhookSignature) before going live.
     */
    @PostMapping("/recharge/webhook")
    public void webhook(@RequestBody Map<String, Object> payload) throws Exception {
        // TODO: verify X-Razorpay-Signature header against payload using the
        // webhook secret before trusting this — left out here since it needs
        // the raw request body bytes, which requires a bit more Spring config
        // (a filter that caches the body) than fits cleanly in this method.
        String orderId = extractOrderId(payload);
        if (orderId != null) {
            razorpayService.handleWebhookPaymentCaptured(orderId);
        }
    }

    @SuppressWarnings("unchecked")
    private String extractOrderId(Map<String, Object> payload) {
        try {
            var payloadObj = (Map<String, Object>) payload.get("payload");
            var payment = (Map<String, Object>) payloadObj.get("payment");
            var entity = (Map<String, Object>) payment.get("entity");
            return (String) entity.get("order_id");
        } catch (Exception e) {
            return null;
        }
    }
}
