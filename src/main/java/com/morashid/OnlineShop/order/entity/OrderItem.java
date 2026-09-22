package com.morashid.OnlineShop.order.entity;

import com.morashid.OnlineShop.product.entity.Product;
import com.morashid.OnlineShop.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * OrderItem entity - inawakilisha table "order_items".
 * 
 * MUHIMU: OrderItem ina snapshot ya data wakati wa kununua:
 *   - price     : bei ya product WAKATI WA KUNUNUA (si ya sasa!)
 *   - sellerId  : muuzaji wa product hiyo (kwa tracking)
 *   - productName : jina la product (kama limebadilika baadaye)
 * 
 * Hii ni kwa sababu:
 *   - Seller anaweza kubadilisha price baadaye
 *   - Product inaweza kufutwa baadaye
 *   - Order ni "historical record" — haiwezi kubadilika!
 */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * Product iliyonunuliwa.
     * 
     * Kumbuka: Tuna-refer Product kwa ID, lakini tunahifadhi pia snapshot
     * ya price kwa sababu product price inaweza kubadilika.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Seller wa product hii.
     * 
     * Tuna-hifadhi seller kwa sababu:
     *   - Order moja inaweza kuwa na products za sellers tofauti
     *   - Kila seller anaona orders zenye products ZAKE
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    /**
     * Snapshot ya product name wakati wa kununua.
     */
    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    /**
     * Idadi iliyonunuliwa.
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * MUHIMU: Bei ya product WAKATI WA KUNUNUA (snapshot).
     * 
     * Hii HAIbadiliki hata product price ibadilike baadaye.
     */
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * Helper method - subtotal (price × quantity).
     */
    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}