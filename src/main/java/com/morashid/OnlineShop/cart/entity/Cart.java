package com.morashid.OnlineShop.cart.entity;

import com.morashid.OnlineShop.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Cart entity - inawakilisha table "carts".
 * 
 * Relationship:
 *   - @OneToOne User (BUYER) : Buyer mmoja ana cart moja
 *   - @OneToMany CartItem    : Cart moja ina items nyingi
 * 
 * Kila buyer ana cart moja tu. Cart inaundwa automatically wakati
 * buyer anaongeza item ya kwanza.
 */
@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Buyer anayemiliki cart hii.
     * 
     * @OneToOne    : Buyer mmoja → Cart moja
     * @JoinColumn  : Foreign key column "buyer_id"
     * 
     * unique = true → Hakuna buyer mwenye carts mbili
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false, unique = true)
    private User buyer;

    /**
     * Items zilizo kwenye cart.
     * 
     * cascade = ALL, orphanRemoval = true :
     *   - Ukifuta Cart, CartItems zote zinafutwa pia
     *   - Ukiondoa CartItem kwenye list, inafutwa kwenye DB
     * 
     * Hii ni muhimu! Bila cascade, tungekuwa na orphans (CartItems bila Cart).
     */
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Helper method - ongeza item kwenye cart.
     */
    public void addItem(CartItem item) {
        items.add(item);
        item.setCart(this);
    }

    /**
     * Helper method - ondoa item kwenye cart.
     */
    public void removeItem(CartItem item) {
        items.remove(item);
        item.setCart(null);
    }

    /**
     * Helper method - hesabu total ya cart (kwa display tu).
     * 
     * MUHIMU: Total ya order (Phase 10) inahesabiwa upya server-side!
     */
    public java.math.BigDecimal getTotal() {
        return items.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }
}