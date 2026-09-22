package com.morashid.OnlineShop.product.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private List<String> imageUrls;  // ← Badilisha kutoka imageUrl

    private Long categoryId;
    private String categoryName;

    private Long sellerId;
    private String sellerName;

    private LocalDateTime createdAt;
}