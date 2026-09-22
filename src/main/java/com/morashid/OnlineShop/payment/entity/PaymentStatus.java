package com.morashid.OnlineShop.payment.entity;

/**
 * PaymentStatus - hali za malipo.
 * 
 * Flow MPYA:
 *   PENDING → (buyer analipa) → AWAITING_CONFIRMATION 
 *           → (admin anathibitisha) → COMPLETED
 * 
 *   PENDING → FAILED
 *   COMPLETED → REFUNDED
 */
public enum PaymentStatus {
    PENDING,                 // Payment imeundwa, inasubiri buyer alipe
    AWAITING_CONFIRMATION,   // Buyer kasema amelipa, admin anathibitisha
    COMPLETED,               // Admin amethibitisha malipo
    FAILED,                  // Malipo yameshindwa
    REFUNDED                 // Malipo yamerudishwa
}