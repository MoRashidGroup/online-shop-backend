package com.morashid.OnlineShop.payment.dto;

import com.morashid.OnlineShop.payment.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO ya kuunda payment.
 * 
 * MUHIMU: Hatu-trust:
 *   - amount      (tunachukua kutoka order.totalAmount)
 *   - status      (default = COMPLETED baada ya malipo)
 *   - paidAt      (server inaweka)
 * 
 * Tunachukua tu:
 *   - orderId
 *   - method
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod method;
}