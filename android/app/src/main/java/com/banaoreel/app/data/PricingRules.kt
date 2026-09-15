package com.banaoreel.app.data

/**
 * Client-side mirror of the pricing shown to the user before they confirm.
 * Source of truth is the backend; this is only for instant UI feedback.
 */
object PricingRules {
    fun costForDurationPaise(durationSec: Int): Int = when {
        durationSec <= 15 -> 500   // Rs 5
        durationSec <= 30 -> 1000  // Rs 10
        else -> 1000 + (durationSec - 30) * 50 // placeholder for future longer tiers
    }
}
