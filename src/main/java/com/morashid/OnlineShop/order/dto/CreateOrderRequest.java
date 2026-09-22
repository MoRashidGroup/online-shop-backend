package com.morashid.OnlineShop.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO ya kuunda order.
 * 
 * MUHIMU: Hatu-trust:
 *   - totalAmount (inahesabiwa server-side)
 *   - items (tunachukua kutoka cart)
 *   - prices (tunachukua kutoka products)
 * 
 * Tunachukua tu:
 *   - shippingAddress
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotBlank(message = "Shipping address is required")
    @Size(min = 10, max = 500, message = "Shipping address must be between 10 and 500 characters")
    private String shippingAddress;
}