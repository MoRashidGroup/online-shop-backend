package com.morashid.OnlineShop.cart.service;

import com.morashid.OnlineShop.cart.dto.AddToCartRequest;
import com.morashid.OnlineShop.cart.dto.CartItemResponse;
import com.morashid.OnlineShop.cart.dto.CartResponse;
import com.morashid.OnlineShop.cart.entity.Cart;
import com.morashid.OnlineShop.cart.entity.CartItem;
import com.morashid.OnlineShop.cart.repository.CartItemRepository;
import com.morashid.OnlineShop.cart.repository.CartRepository;
import com.morashid.OnlineShop.exception.BadRequestException;
import com.morashid.OnlineShop.exception.ResourceNotFoundException;
import com.morashid.OnlineShop.product.entity.Product;
import com.morashid.OnlineShop.product.repository.ProductRepository;
import com.morashid.OnlineShop.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CartService - business logic ya cart.
 * 
 * Rules:
 *   - Kila buyer ana cart MOJA
 *   - Cart inaundwa automatically anapoongeza item ya kwanza
 *   - Kuongeza product iliyopo → quantity inaongezwa
 *   - Haiwezi kuongeza zaidi ya stock iliyopo
 *   - Quantity >= 1 (kama 0 → futa CartItem)
 */
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    /**
     * Pata cart ya buyer (au unda mpya kama haipo).
     */
    @Transactional
    public Cart getOrCreateCart(User buyer) {
        return cartRepository.findByBuyerId(buyer.getId())
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .buyer(buyer)
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    /**
     * Pata cart ya buyer kama CartResponse.
     */
    @Transactional
    public CartResponse getCart(User buyer) {
        Cart cart = getOrCreateCart(buyer);
        return toResponse(cart);
    }

    /**
     * Ongeza item kwenye cart.
     * 
     * Flow:
     *   1. Tafuta product → 404 kama haipo
     *   2. Check kama product ina stock ya kutosha
     *   3. Pata au unda cart
     *   4. Kama product ipo tayari kwenye cart → ongeza quantity
     *   5. Kama haipo → unda CartItem mpya
     *   6. Save
     */
    @Transactional
    public CartResponse addToCart(AddToCartRequest request, User buyer) {

        // 1. Tafuta product
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        // 2. Check stock
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new BadRequestException(
                    "Insufficient stock. Available: " + product.getStockQuantity());
        }

        // 3. Pata au unda cart
        Cart cart = getOrCreateCart(buyer);

        // 4. Check kama product ipo tayari kwenye cart
        CartItem existingItem = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        if (existingItem != null) {
            // Ongeza quantity (na check stock mpya)
            int newQuantity = existingItem.getQuantity() + request.getQuantity();

            if (product.getStockQuantity() < newQuantity) {
                throw new BadRequestException(
                        "Insufficient stock. Available: " + product.getStockQuantity()
                        + ", in cart already: " + existingItem.getQuantity());
            }

            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        } else {
            // Unda CartItem mpya
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cartItemRepository.save(newItem);
            cart.getItems().add(newItem);
        }

        // Reload cart ili kupata items zote
        Cart updatedCart = cartRepository.findById(cart.getId()).orElseThrow();
        return toResponse(updatedCart);
    }

    /**
     * Update quantity ya CartItem.
     */
    @Transactional
    public CartResponse updateCartItem(Long cartItemId, Integer quantity, User buyer) {

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        // Check ni ya buyer huyu
        if (!cartItem.getCart().getBuyer().getId().equals(buyer.getId())) {
            throw new BadRequestException("This cart item does not belong to you");
        }

        // Check stock
        Product product = cartItem.getProduct();
        if (product.getStockQuantity() < quantity) {
            throw new BadRequestException(
                    "Insufficient stock. Available: " + product.getStockQuantity());
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        Cart cart = cartRepository.findById(cartItem.getCart().getId()).orElseThrow();
        return toResponse(cart);
    }

   /**
 * Futa CartItem.
 */
    @Transactional
    public void removeCartItem(Long cartItemId, User buyer) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        // Check ni ya buyer huyu
        if (!cartItem.getCart().getBuyer().getId().equals(buyer.getId())) {
            throw new BadRequestException("This cart item does not belong to you");
        }

        // Futa item
        cartItemRepository.delete(cartItem);
        cartItemRepository.flush();  // ← MUHIMU: Force DB deletion sasa hivi
    }

    /**
     * Futa cart yote (items zote).
     */
    @Transactional
    public void clearCart(User buyer) {
        Cart cart = getOrCreateCart(buyer);
        cartItemRepository.deleteByCartId(cart.getId());
    }

    /**
     * Helper method - convert Cart entity → CartResponse.
     */
    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        int totalItems = items.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        BigDecimal totalAmount = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .id(cart.getId())
                .buyerId(cart.getBuyer().getId())
                .items(items)
                .totalItems(totalItems)
                .totalAmount(totalAmount)
                .createdAt(cart.getCreatedAt())
                .build();
    }

    private CartItemResponse toItemResponse(CartItem item) {
        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .productImageUrl(
                    item.getProduct().getImageUrls() != null 
                        && !item.getProduct().getImageUrls().isEmpty()
                        ? item.getProduct().getImageUrls().get(0)  // ← Picha ya kwanza
                        : null
                )
                .productPrice(item.getProduct().getPrice())
                .quantity(item.getQuantity())
                .subtotal(item.getSubtotal())
                .availableStock(item.getProduct().getStockQuantity())
                .build();
    }
}