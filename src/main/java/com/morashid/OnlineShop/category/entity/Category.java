package com.morashid.OnlineShop.category.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import com.morashid.OnlineShop.product.entity.Product;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Category entity - inawakilisha table "categories".
 * 
 * Kila category ina:
 *   - name (jina la rafu)
 *   - description (maelezo)
 * 
 * Relationship:
 *   Category (1) ────< Products (Many)
 *   (Tutaongeza products list Phase 8)
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Jina la category - LAZIMA iwe unique.
     * Mfano: "Electronics", "Clothing", "Books"
     * 
     * unique = true → Haiwezi kuwa na categories mbili zenye jina moja.
     */
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    /**
     * Maelezo ya category.
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * Muda wa kuunda category.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // NOTE: Tutaongeza hii Phase 8:
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products;
}