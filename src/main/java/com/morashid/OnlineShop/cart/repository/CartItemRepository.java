package com.morashid.OnlineShop.cart.repository;

import com.morashid.OnlineShop.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    /**
     * Tafuta CartItem kwa cart na product.
     * 
     * Inatumika ku-check kama product ipo tayari kwenye cart.
     * 
     * SQL: SELECT * FROM cart_items WHERE cart_id = ? AND product_id = ?
     */
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    /**
     * Futa CartItems zote za cart.
     * 
     * Inatumika baada ya ku-onda order (Phase 10).
     */
    void deleteByCartId(Long cartId);
}