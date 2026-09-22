package com.morashid.OnlineShop.cart.repository;

import com.morashid.OnlineShop.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Tafuta cart kwa buyer ID.
     * 
     * SQL: SELECT * FROM carts WHERE buyer_id = ?
     */
    Optional<Cart> findByBuyerId(Long buyerId);

    /**
     * Angalia kama buyer ana cart.
     */
    boolean existsByBuyerId(Long buyerId);
}