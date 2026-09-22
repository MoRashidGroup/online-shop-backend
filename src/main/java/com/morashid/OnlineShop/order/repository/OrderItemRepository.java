package com.morashid.OnlineShop.order.repository;

import com.morashid.OnlineShop.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * Pata order items kwa seller.
     * 
     * Inatumika kwa sellers kuona products zao zilizonunuliwa.
     * 
     * SQL: SELECT * FROM order_items WHERE seller_id = ?
     */
    List<OrderItem> findBySellerId(Long sellerId);
}