package com.morashid.OnlineShop.order.entity;

import com.morashid.OnlineShop.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order entity - inawakilisha table "orders".
 * 
 * Relationship:
 *   - @ManyToOne User (BUYER) : Order nyingi za buyer mmoja
 *   - @OneToMany OrderItem    : Order moja ina items nyingi
 * 
 * MUHIMU: total_amount inahesabiwa SERVER-side wakati wa kuunda order.
 * HATUTRUST total_amount kutoka frontend!
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Buyer aliyefanya order.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    /**
     * Total ya order.
     * 
     * MUHIMU: Inahesabiwa server-side kutoka OrderItems!
     * HATUTRUST value kutoka frontend.
     */
    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    /**
     * Status ya order.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderStatus status;

    /**
     * Anwani ya kufikisha bidhaa.
     */
    @Column(name = "shipping_address", nullable = false, length = 500)
    private String shippingAddress;

    /**
     * Items za order.
     * 
     * cascade = ALL : Ukifuta order, items zinafutwa pia
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Helper method - ongeza item.
     */
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}