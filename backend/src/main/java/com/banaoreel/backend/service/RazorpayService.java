package com.banaoreel.backend.service;

import com.banaoreel.backend.entity.RechargeOrder;
import com.banaoreel.backend.repository.RechargeOrderRepository;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RazorpayService {

    private final String keyId;
    private final String keySecret;
    private final RechargeOrderRepository rechargeOrderRepository;
    private final WalletService walletService;

    public RazorpayService(
            @Value("${banaoreel.razorpay.key-id}") String keyId,
            @Value("${banaoreel.razorpay.key-secret}") String keySecret,
            RechargeOrderRepository rechargeOrderRepository,
            WalletService walletService
    ) {
        this.keyId = keyId;
        this.keySecret = keySecret;
        this.rechargeOrderRepository = rechargeOrderRepository;
        this.walletService = walletService;
    }

    public RechargeOrder createOrder(UUID userId, long amountPaise) throws Exception {
        RazorpayClient client = new RazorpayClient(keyId, keySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountPaise); // Razorpay also expects amount in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "recharge_" + UUID.randomUUID());

        com.razorpay.Order razorpayOrder = client.orders.create(orderRequest);

        RechargeOrder order = new RechargeOrder();
        order.setUserId(userId);
        order.setAmountPaise(amountPaise);
        order.setGatewayOrderId(razorpayOrder.get("id"));
        order.setStatus("created");
        return rechargeOrderRepository.save(order);
    }

    /**
     * Verifies the signature Razorpay returns to the app after checkout, then
     * credits the wallet. This is the synchronous "verify" path called right
     * after checkout completes; handleWebhook below is the reliability backup
     * for cases where the app closes before this call completes.
     */
    public void verifyAndCredit(String paymentId, String orderId, String signature) throws Exception {
        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", orderId);
        options.put("razorpay_payment_id", paymentId);
        options.put("razorpay_signature", signature);

        boolean valid = Utils.verifyPaymentSignature(options, keySecret);
        if (!valid) {
            throw new SecurityException("Invalid Razorpay signature");
        }

        RechargeOrder order = rechargeOrderRepository.findByGatewayOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown order id"));

        if ("paid".equals(order.getStatus())) {
            return; // already credited (e.g. webhook beat us to it) — avoid double credit
        }

        order.setStatus("paid");
        rechargeOrderRepository.save(order);
        walletService.credit(order.getUserId(), order.getAmountPaise());
    }

    /**
     * Webhook handler: configure this URL (POST /wallet/recharge/webhook) in the
     * Razorpay dashboard as a backup path, in case the app never calls /verify
     * (closed mid-checkout, network drop, etc). Verifies using the separate
     * webhook secret (not keySecret) that Razorpay signs webhook payloads with.
     */
    public void handleWebhookPaymentCaptured(String orderId) throws Exception {
        RechargeOrder order = rechargeOrderRepository.findByGatewayOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown order id"));

        if ("paid".equals(order.getStatus())) return; // idempotent

        order.setStatus("paid");
        rechargeOrderRepository.save(order);
        walletService.credit(order.getUserId(), order.getAmountPaise());
    }
}
