package com.morashid.OnlineShop.payment.entity;

/**
 * PaymentMethod - njia za malipo.
 * 
 * MOBILE_MONEY imegawanywa kwa mitandao:
 *   - MPESA
 *   - TIGO_PESA
 *   - AIRTEL_MONEY
 *   - HALOTEL_MONEY
 */
public enum PaymentMethod {
    MPESA,
    TIGO_PESA,
    AIRTEL_MONEY,
    HALOTEL_MONEY,
    CASH,
    CREDIT_CARD,
    BANK_TRANSFER
}