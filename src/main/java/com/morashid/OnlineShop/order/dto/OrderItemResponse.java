package com.morashid.OnlineShop.order.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private Long sellerId;
    private String sellerName;
    private Integer quantity;
    private BigDecimal price;      // Snapshot price
    private BigDecimal subtotal;
}