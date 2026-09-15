package com.banaoreel.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Single source of truth for pricing. The Android app shows an estimate
 * client-side (PricingRules.kt) purely for instant UI feedback — this is
 * what actually gets charged.
 */
@Service
public class PricingService {

    private final long tier15sPaise;
    private final long tier30sPaise;

    public PricingService(
            @Value("${banaoreel.pricing.tier-15s-paise}") long tier15sPaise,
            @Value("${banaoreel.pricing.tier-30s-paise}") long tier30sPaise
    ) {
        this.tier15sPaise = tier15sPaise;
        this.tier30sPaise = tier30sPaise;
    }

    public long costForDurationPaise(int durationSec) {
        if (durationSec <= 15) return tier15sPaise;
        if (durationSec <= 30) return tier30sPaise;
        // Longer-video tiers: TODO, decided later per the product plan.
        throw new IllegalArgumentException("Durations beyond 30s are not yet supported");
    }
}
