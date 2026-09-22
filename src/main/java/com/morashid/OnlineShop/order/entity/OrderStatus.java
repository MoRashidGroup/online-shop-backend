package com.morashid.OnlineShop.order.entity;

/**
 * OrderStatus - hali za order.
 * 
 * Flow ya kawaida:
 *   PENDING → PROCESSING → SHIPPED → DELIVERED
 * 
 * Order inaweza kubadilishwa kuwa CANCELLED kabla ya SHIPPED.
 */
public enum OrderStatus {
    PENDING,       // Order imeundwa, inasubiri seller kuthibitisha
    PROCESSING,    // Seller anaiandaa order
    SHIPPED,       // Order imetumwa
    DELIVERED,     // Order imefika kwa buyer
    CANCELLED      // Order imefutwa
}