package com.morashid.OnlineShop.order.controller;

import com.morashid.OnlineShop.auth.security.CustomUserDetails;
import com.morashid.OnlineShop.common.ApiResponse;
import com.morashid.OnlineShop.order.dto.CreateOrderRequest;
import com.morashid.OnlineShop.order.dto.OrderResponse;
import com.morashid.OnlineShop.order.entity.OrderStatus;
import com.morashid.OnlineShop.order.service.OrderService;
import com.morashid.OnlineShop.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * OrderController - REST endpoints za orders.
 * 
 * BUYER:
 *   - POST /api/orders             → Unda order kutoka cart
 *   - GET  /api/orders/my-orders   → Orders zangu
 *   - GET  /api/orders/{id}        → Order moja
 * 
 * SELLER:
 *   - GET  /api/seller/orders              → Orders zenye products zangu
 *   - PUT  /api/seller/orders/{id}/status  → Update status
 * 
 * ADMIN:
 *   - GET  /api/admin/orders       → Orders zote
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // ============================================
    // BUYER ENDPOINTS
    // ============================================

    /**
     * POST /api/orders
     * Unda order kutoka cart ya buyer.
     */
    @PostMapping("/orders")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ApiResponse<OrderResponse>> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User buyer = userDetails.getUser();
        OrderResponse order = orderService.createOrder(request, buyer);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order created successfully", order));
    }

    /**
     * GET /api/orders/my-orders
     * Pata orders zangu.
     */
    @GetMapping("/orders/my-orders")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User buyer = userDetails.getUser();
        List<OrderResponse> orders = orderService.getMyOrders(buyer);
        return ResponseEntity.ok(ApiResponse.success("My orders retrieved", orders));
    }

    /**
     * GET /api/orders/{id}
     * Pata order moja (buyer anaweza kuona yake pekee).
     */
    @GetMapping("/orders/{id}")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        OrderResponse order = orderService.getOrderById(id, user);
        return ResponseEntity.ok(ApiResponse.success("Order retrieved", order));
    }

    // ============================================
    // SELLER ENDPOINTS
    // ============================================

    /**
     * GET /api/seller/orders
     * Seller: Pata orders zenye products zake.
     */
    @GetMapping("/seller/orders")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getSellerOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User seller = userDetails.getUser();
        List<OrderResponse> orders = orderService.getSellerOrders(seller);
        return ResponseEntity.ok(ApiResponse.success("Seller orders retrieved", orders));
    }

    /**
     * PUT /api/seller/orders/{id}/status
     * Seller: Update status ya order yenye products zake.
     */
    @PutMapping("/seller/orders/{id}/status")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String statusStr = body.get("status");
        if (statusStr == null) {
            throw new IllegalArgumentException("Status is required");
        }

        OrderStatus status;
        try {
            status = OrderStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid status. Allowed: PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED");
        }

        User seller = userDetails.getUser();
        OrderResponse order = orderService.updateOrderStatus(id, status, seller);
        return ResponseEntity.ok(ApiResponse.success("Order status updated", order));
    }

    // ============================================
    // ADMIN ENDPOINTS
    // ============================================

    /**
     * GET /api/admin/orders
     * Admin: Pata orders zote.
     */
    @GetMapping("/admin/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getAllOrders() {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.success("All orders retrieved", orders));
    }
}