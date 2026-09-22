package com.morashid.OnlineShop.payment.repository;

import com.morashid.OnlineShop.payment.entity.Payment;
import com.morashid.OnlineShop.payment.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Tafuta payment kwa order ID.
     * 
     * SQL: SELECT * FROM payments WHERE order_id = ?
     */
    Optional<Payment> findByOrderId(Long orderId);

    /**
     * Angalia kama order ina payment.
     */
    boolean existsByOrderId(Long orderId);

    /**
     * Tafuta payments kwa status.
     */
    List<Payment> findByStatus(PaymentStatus status);
}