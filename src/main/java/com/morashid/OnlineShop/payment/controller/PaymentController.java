package com.morashid.OnlineShop.payment.controller;

import com.morashid.OnlineShop.auth.security.CustomUserDetails;
import com.morashid.OnlineShop.common.ApiResponse;
import com.morashid.OnlineShop.payment.dto.ConfirmPaymentRequest;
import com.morashid.OnlineShop.payment.dto.PaymentRequest;
import com.morashid.OnlineShop.payment.dto.PaymentResponse;
import com.morashid.OnlineShop.payment.service.PaymentService;
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

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // ============================================
    // BUYER
    // ============================================

    /**
     * POST /api/payments
     * Unda payment (status = PENDING).
     */
    @PostMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @Valid @RequestBody PaymentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User buyer = userDetails.getUser();
        PaymentResponse payment = paymentService.createPayment(request, buyer);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Payment initiated", payment));
    }

    /**
     * POST /api/payments/{id}/confirm
     * Buyer: "Nimelipa" - inform admin.
     */
    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<ApiResponse<PaymentResponse>> confirmPayment(
            @PathVariable Long id,
            @Valid @RequestBody ConfirmPaymentRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User buyer = userDetails.getUser();
        PaymentResponse payment = paymentService.confirmPayment(id, request, buyer);

        return ResponseEntity.ok(ApiResponse.success(
                "Malipo yamewasilishwa kwa admin. Subiri uthibitisho.",
                payment));
    }

    // ============================================
    // GETTERS
    // ============================================

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        PaymentResponse payment = paymentService.getPaymentById(id, user);
        return ResponseEntity.ok(ApiResponse.success("Payment retrieved", payment));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByOrderId(
            @PathVariable Long orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userDetails.getUser();
        PaymentResponse payment = paymentService.getPaymentByOrderId(orderId, user);
        return ResponseEntity.ok(ApiResponse.success("Payment retrieved", payment));
    }

    // ============================================
    // ADMIN
    // ============================================

    /**
     * GET /api/admin/payments/pending
     * Admin: Pata payments zinazosubiri uthibitisho.
     */
    @GetMapping("/admin/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPendingPayments() {
        List<PaymentResponse> payments = paymentService.getPendingPayments();
        return ResponseEntity.ok(ApiResponse.success("Pending payments", payments));
    }

    /**
     * POST /api/admin/payments/{id}/confirm
     * Admin: Thibitisha malipo.
     */
    @PostMapping("/admin/{id}/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> adminConfirmPayment(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User admin = userDetails.getUser();
        PaymentResponse payment = paymentService.adminConfirmPayment(id, admin);

        return ResponseEntity.ok(ApiResponse.success("Payment confirmed", payment));
    }

    /**
     * POST /api/admin/payments/{id}/reject
     * Admin: Kataa malipo.
     */
    @PostMapping("/admin/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PaymentResponse>> adminRejectPayment(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String reason = body != null ? body.get("reason") : null;
        User admin = userDetails.getUser();
        PaymentResponse payment = paymentService.adminRejectPayment(id, reason, admin);

        return ResponseEntity.ok(ApiResponse.success("Payment rejected", payment));
    }
}