package com.morashid.OnlineShop.payment.service;

import com.morashid.OnlineShop.exception.BadRequestException;
import com.morashid.OnlineShop.exception.ResourceNotFoundException;
import com.morashid.OnlineShop.order.entity.Order;
import com.morashid.OnlineShop.order.repository.OrderRepository;
import com.morashid.OnlineShop.payment.dto.ConfirmPaymentRequest;
import com.morashid.OnlineShop.payment.dto.PaymentRequest;
import com.morashid.OnlineShop.payment.dto.PaymentResponse;
import com.morashid.OnlineShop.payment.entity.Payment;
import com.morashid.OnlineShop.payment.entity.PaymentMethod;
import com.morashid.OnlineShop.payment.entity.PaymentStatus;
import com.morashid.OnlineShop.payment.repository.PaymentRepository;
import com.morashid.OnlineShop.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    // Admin mobile money numbers (kutoka application.properties)
    @Value("${app.payment.mpesa.number:0754000001}")
    private String mpesaNumber;
    @Value("${app.payment.mpesa.name:OnlineShop Admin}")
    private String mpesaName;

    @Value("${app.payment.tigo.number:0654000002}")
    private String tigoNumber;
    @Value("${app.payment.tigo.name:OnlineShop Admin}")
    private String tigoName;

    @Value("${app.payment.airtel.number:0784000003}")
    private String airtelNumber;
    @Value("${app.payment.airtel.name:OnlineShop Admin}")
    private String airtelName;

    @Value("${app.payment.halotel.number:0624000004}")
    private String halotelNumber;
    @Value("${app.payment.halotel.name:OnlineShop Admin}")
    private String halotelName;

    // ============================================
    // CREATE PAYMENT (status = PENDING)
    // ============================================
    @Transactional
    public PaymentResponse createPayment(PaymentRequest request, User buyer) {

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", request.getOrderId()));

        if (!order.getBuyer().getId().equals(buyer.getId())) {
            throw new BadRequestException("You can only pay for your own orders");
        }

        if (paymentRepository.existsByOrderId(order.getId())) {
            throw new BadRequestException("Order already has a payment");
        }

        // Chagua namba ya admin kulingana na method
        Map<String, String> adminInfo = getAdminPaymentInfo(request.getMethod());

        Payment payment = Payment.builder()
                .order(order)
                .amount(order.getTotalAmount())
                .method(request.getMethod())
                .status(PaymentStatus.PENDING)  // ← PENDING, si COMPLETED!
                .paymentNumber(adminInfo.get("number"))
                .paymentName(adminInfo.get("name"))
                .build();

        Payment saved = paymentRepository.save(payment);
        return toResponse(saved);
    }

    // ============================================
    // BUYER: "Nimelipa"
    // ============================================
    @Transactional
    public PaymentResponse confirmPayment(Long paymentId, ConfirmPaymentRequest request, User buyer) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));

        if (!payment.getOrder().getBuyer().getId().equals(buyer.getId())) {
            throw new BadRequestException("This payment does not belong to you");
        }

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BadRequestException("Payment is not in PENDING state. Current: " + payment.getStatus());
        }

        payment.setPayerPhoneNumber(request.getPayerPhoneNumber());
        payment.setBuyerConfirmedAt(LocalDateTime.now());
        payment.setStatus(PaymentStatus.AWAITING_CONFIRMATION);
        
        if (request.getTransactionReference() != null && !request.getTransactionReference().isBlank()) {
            payment.setTransactionReference(request.getTransactionReference());
        }

        Payment updated = paymentRepository.save(payment);
        return toResponse(updated);
    }

    // ============================================
    // ADMIN: Confirm payment
    // ============================================
    @Transactional
    public PaymentResponse adminConfirmPayment(Long paymentId, User admin) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new BadRequestException("Payment already completed");
        }

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());
        payment.setAdminConfirmedAt(LocalDateTime.now());
        payment.setConfirmedByAdmin(admin);

        // Tengeneza transaction reference kama haipo
        if (payment.getTransactionReference() == null || payment.getTransactionReference().isBlank()) {
            payment.setTransactionReference(generateTransactionReference(payment.getMethod()));
        }

        Payment updated = paymentRepository.save(payment);
        return toResponse(updated);
    }

    // ============================================
    // ADMIN: Reject/Fail payment
    // ============================================
    @Transactional
    public PaymentResponse adminRejectPayment(Long paymentId, String reason, User admin) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));

        payment.setStatus(PaymentStatus.FAILED);
        if (reason != null && !reason.isBlank()) {
            payment.setTransactionReference("REJECTED: " + reason);
        }

        Payment updated = paymentRepository.save(payment);
        return toResponse(updated);
    }

    // ============================================
    // ADMIN: Pata payments zote zinazosubiri
    // ============================================
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPendingPayments() {
        return paymentRepository.findByStatus(PaymentStatus.AWAITING_CONFIRMATION)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ============================================
    // GETTERS
    // ============================================
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(Long orderId, User user) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "orderId", orderId));

        if (user.getRole().name().equals("BUYER")
                && !payment.getOrder().getBuyer().getId().equals(user.getId())) {
            throw new BadRequestException("You can only view your own payments");
        }

        return toResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id, User user) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));

        if (user.getRole().name().equals("BUYER")
                && !payment.getOrder().getBuyer().getId().equals(user.getId())) {
            throw new BadRequestException("You can only view your own payments");
        }

        return toResponse(payment);
    }

    // ============================================
    // HELPERS
    // ============================================
    private Map<String, String> getAdminPaymentInfo(PaymentMethod method) {
        Map<String, String> info = new HashMap<>();
        
        switch (method) {
            case MPESA -> {
                info.put("number", mpesaNumber);
                info.put("name", mpesaName);
            }
            case TIGO_PESA -> {
                info.put("number", tigoNumber);
                info.put("name", tigoName);
            }
            case AIRTEL_MONEY -> {
                info.put("number", airtelNumber);
                info.put("name", airtelName);
            }
            case HALOTEL_MONEY -> {
                info.put("number", halotelNumber);
                info.put("name", halotelName);
            }
            default -> {
                info.put("number", "N/A");
                info.put("name", "Contact Admin");
            }
        }
        
        return info;
    }

    private String generateTransactionReference(PaymentMethod method) {
        String prefix = method.name().substring(0, 3).toUpperCase();
        String uniquePart = UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        return prefix + "-" + uniquePart;
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .transactionReference(payment.getTransactionReference())
                .paymentNumber(payment.getPaymentNumber())
                .paymentName(payment.getPaymentName())
                .payerPhoneNumber(payment.getPayerPhoneNumber())
                .buyerConfirmedAt(payment.getBuyerConfirmedAt())
                .adminConfirmedAt(payment.getAdminConfirmedAt())
                .confirmedByAdminName(
                        payment.getConfirmedByAdmin() != null 
                                ? payment.getConfirmedByAdmin().getFullName() 
                                : null
                )
                .paidAt(payment.getPaidAt())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}