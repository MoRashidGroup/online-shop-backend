package com.morashid.OnlineShop.cart.entity;

import com.morashid.OnlineShop.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

/**
 * CartItem entity - inawakilisha table "cart_items".
 * 
 * Kila CartItem ni bidhaa MOJA kwenye cart. Kama buyer anaongeza
 * product hiyo hiyo mara mbili, quantity inaongezwa (si kuunda CartItem mpya).
 * 
 * Relationships:
 *   - @ManyToOne Cart    : CartItem nyingi zinaweza kuwa cart moja
 *   - @ManyToOne Product : CartItem nyingi zinaweza kuwa product moja
 */
@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Idadi ya product kwenye cart.
     * 
     * MUHIMU: Haiwezi kuwa 0 au negative. Kama buyer anataka ku-remove,
     * tunafuta CartItem yote (si kuweka 0).
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * Helper method - hesabu subtotal (price × quantity).
     */
    public java.math.BigDecimal getSubtotal() {
        return product.getPrice().multiply(java.math.BigDecimal.valueOf(quantity));
    }
}