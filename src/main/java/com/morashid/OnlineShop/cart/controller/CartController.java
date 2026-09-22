package com.morashid.OnlineShop.cart.controller;

import com.morashid.OnlineShop.auth.security.CustomUserDetails;
import com.morashid.OnlineShop.cart.dto.AddToCartRequest;
import com.morashid.OnlineShop.cart.dto.CartResponse;
import com.morashid.OnlineShop.cart.dto.UpdateCartItemRequest;
import com.morashid.OnlineShop.cart.service.CartService;
import com.morashid.OnlineShop.common.ApiResponse;
import com.morashid.OnlineShop.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * CartController - REST endpoints za cart (BUYER only).
 * 
 * Endpoints:
 *   - GET    /api/cart                    → Pata cart
 *   - POST   /api/cart/items              → Ongeza item
 *   - PUT    /api/cart/items/{id}         → Update quantity
 *   - DELETE /api/cart/items/{id}         → Futa item
 *   - DELETE /api/cart                    → Futa cart yote (clear)
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('BUYER')")
public class CartController {

    private final CartService cartService;

    /**
     * GET /api/cart
     * Pata cart ya buyer aliye-login.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User buyer = userDetails.getUser();
        CartResponse cart = cartService.getCart(buyer);
        return ResponseEntity.ok(ApiResponse.success("Cart retrieved", cart));
    }

    /**
     * POST /api/cart/items
     * Ongeza item kwenye cart.
     */
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User buyer = userDetails.getUser();
        CartResponse cart = cartService.addToCart(request, buyer);
        return ResponseEntity.ok(ApiResponse.success("Item added to cart", cart));
    }

    /**
     * PUT /api/cart/items/{id}
     * Update quantity ya item.
     */
    @PutMapping("/items/{id}")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCartItemRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User buyer = userDetails.getUser();
        CartResponse cart = cartService.updateCartItem(id, request.getQuantity(), buyer);
        return ResponseEntity.ok(ApiResponse.success("Cart updated", cart));
    }

    /**
     * DELETE /api/cart/items/{id}
     * Futa item kwenye cart.
     */
    @DeleteMapping("/items/{id}")
    public ResponseEntity<ApiResponse<CartResponse>> removeCartItem(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User buyer = userDetails.getUser();
        cartService.removeCartItem(id, buyer);
        // Rudisha cart mpya (fresh kutoka DB)
        CartResponse cart = cartService.getCart(buyer);
        return ResponseEntity.ok(ApiResponse.success("Item removed", cart));
    }

    /**
     * DELETE /api/cart
     * Futa items zote kwenye cart (clear).
     */
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> clearCart(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User buyer = userDetails.getUser();
        cartService.clearCart(buyer);
        return ResponseEntity.ok(ApiResponse.success("Cart cleared"));
    }
}