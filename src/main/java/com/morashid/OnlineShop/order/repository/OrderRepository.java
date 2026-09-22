package com.morashid.OnlineShop.order.repository;

import com.morashid.OnlineShop.order.entity.Order;
import com.morashid.OnlineShop.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Pata orders za buyer fulani.
     * 
     * SQL: SELECT * FROM orders WHERE buyer_id = ? ORDER BY created_at DESC
     */
    List<Order> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);

    /**
     * Pata orders kwa status.
     */
    List<Order> findByStatus(OrderStatus status);

    /**
     * Pata orders za buyer kwa status.
     */
    List<Order> findByBuyerIdAndStatus(Long buyerId, OrderStatus status);
}