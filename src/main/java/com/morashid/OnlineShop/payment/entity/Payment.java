package com.morashid.OnlineShop.payment.entity;

import com.morashid.OnlineShop.order.entity.Order;
import com.morashid.OnlineShop.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false, length = 30)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PaymentStatus status;

    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;

    /**
     * Namba ya simu ya admin (display kwa buyer).
     * Snapshot wakati payment inaundwa.
     */
    @Column(name = "payment_number", length = 20)
    private String paymentNumber;

    /**
     * Jina la admin (display).
     */
    @Column(name = "payment_name", length = 100)
    private String paymentName;

    /**
     * Namba ya simu ya buyer aliyotumia kulipa.
     */
    @Column(name = "payer_phone_number", length = 20)
    private String payerPhoneNumber;

    /**
     * Muda buyer aliposema "nimelipa".
     */
    @Column(name = "buyer_confirmed_at")
    private LocalDateTime buyerConfirmedAt;

    /**
     * Muda admin alithibitisha malipo.
     */
    @Column(name = "admin_confirmed_at")
    private LocalDateTime adminConfirmedAt;

    /**
     * Admin aliyethibitisha.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "confirmed_by_admin_id")
    private User confirmedByAdmin;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}