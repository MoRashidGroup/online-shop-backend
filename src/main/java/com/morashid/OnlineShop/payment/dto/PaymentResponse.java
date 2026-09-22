package com.morashid.OnlineShop.payment.dto;

import com.morashid.OnlineShop.payment.entity.PaymentMethod;
import com.morashid.OnlineShop.payment.entity.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private String transactionReference;

    // Info ya admin (buyer anaona)
    private String paymentNumber;
    private String paymentName;

    // Info ya buyer
    private String payerPhoneNumber;
    private LocalDateTime buyerConfirmedAt;

    // Info ya admin confirmation
    private LocalDateTime adminConfirmedAt;
    private String confirmedByAdminName;

    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
}