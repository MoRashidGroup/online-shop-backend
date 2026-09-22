package com.morashid.OnlineShop.order.service;

import com.morashid.OnlineShop.cart.entity.Cart;
import com.morashid.OnlineShop.cart.entity.CartItem;
import com.morashid.OnlineShop.cart.repository.CartItemRepository;
import com.morashid.OnlineShop.cart.repository.CartRepository;
import com.morashid.OnlineShop.exception.BadRequestException;
import com.morashid.OnlineShop.exception.ResourceNotFoundException;
import com.morashid.OnlineShop.order.dto.CreateOrderRequest;
import com.morashid.OnlineShop.order.dto.OrderItemResponse;
import com.morashid.OnlineShop.order.dto.OrderResponse;
import com.morashid.OnlineShop.order.entity.Order;
import com.morashid.OnlineShop.order.entity.OrderItem;
import com.morashid.OnlineShop.order.entity.OrderStatus;
import com.morashid.OnlineShop.order.repository.OrderItemRepository;
import com.morashid.OnlineShop.order.repository.OrderRepository;
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
 * OrderService - business logic ya orders.
 * 
 * MUHIMU: createOrder ni @Transactional! Hii inahakikisha:
 *   - Cart → Order conversion ni ATOMIC
 *   - Kama kitu kimoja kimeshindwa, kila kitu kina-rollback
 *   - Stock decrease na order creation zinafanyika pamoja
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    /**
     * Unda order kutoka cart ya buyer.
     * 
     * Flow (KILA KITU NI TRANSACTIONAL):
     *   1. Pata cart ya buyer → 400 kama cart haina items
     *   2. Unda Order entity (PENDING)
     *   3. Kwa kila CartItem:
     *      a. Check stock
     *      b. Unda OrderItem na price SNAPSHOT
     *      c. Punguza stock ya product
     *      d. Hesabu subtotal
     *   4. Set total_amount (server-side!)
     *   5. Save order
     *   6. Futa cart items
     *   7. Return OrderResponse
     * 
     * Kama hatua yoyote inashindwa → ROLLBACK yote!
     */
    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, User buyer) {

        // 1. Pata cart
        Cart cart = cartRepository.findByBuyerId(buyer.getId())
                .orElseThrow(() -> new BadRequestException("Cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cart is empty. Add items before ordering.");
        }

        // 2. Unda Order
        Order order = Order.builder()
                .buyer(buyer)
                .shippingAddress(request.getShippingAddress())
                .status(OrderStatus.PENDING)
                .totalAmount(BigDecimal.ZERO)  // Tutahesabu baadaye
                .build();

        // 3. Process kila CartItem
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            // 3a. Check stock
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new BadRequestException(
                        "Insufficient stock for product: " + product.getName()
                        + ". Available: " + product.getStockQuantity()
                        + ", requested: " + cartItem.getQuantity());
            }

            // 3b. Unda OrderItem na SNAPSHOT price
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .seller(product.getSeller())              // Snapshot seller
                    .productName(product.getName())           // Snapshot name
                    .quantity(cartItem.getQuantity())
                    .price(product.getPrice())                // ← SNAPSHOT PRICE!
                    .build();

            order.addItem(orderItem);

            // 3c. Punguza stock
            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);

            // 3d. Hesabu total
            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        // 4. Set total (server-side)
        order.setTotalAmount(totalAmount);

        // 5. Save order
        Order savedOrder = orderRepository.save(order);

        // 6. Futa cart items (cart inabaki, items zinafutwa)
        cartItemRepository.deleteByCartId(cart.getId());

        // 7. Return
        return toResponse(savedOrder);
    }

    /**
     * Pata orders za buyer aliye-login.
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(User buyer) {
        return orderRepository.findByBuyerIdOrderByCreatedAtDesc(buyer.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Pata order moja kwa ID.
     * 
     * MUHIMU: Buyer anaweza kuona order YAKE pekee!
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        // Check ownership (buyer anaweza kuona yake pekee)
        // Admin na seller wanaweza kuona (kupitia endpoints zao)
        if (user.getRole().name().equals("BUYER")
                && !order.getBuyer().getId().equals(user.getId())) {
            throw new BadRequestException("You can only view your own orders");
        }

        return toResponse(order);
    }

    /**
     * Seller: Pata orders zenye products zake.
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getSellerOrders(User seller) {
        // Pata OrderItems za seller
        List<OrderItem> sellerItems = orderItemRepository.findBySellerId(seller.getId());

        // Pata unique orders
        return sellerItems.stream()
                .map(OrderItem::getOrder)
                .distinct()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Seller: Update status ya order.
     * 
     * MUHIMU: Seller anaweza ku-update status TU kama order ina products zake.
     */
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus, User seller) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        // Check kama order ina products za seller huyu
        boolean hasSellerProducts = order.getItems().stream()
                .anyMatch(item -> item.getSeller().getId().equals(seller.getId()));

        if (!hasSellerProducts) {
            throw new BadRequestException("This order does not contain your products");
        }

        order.setStatus(newStatus);
        Order updated = orderRepository.save(order);
        return toResponse(updated);
    }

    /**
     * Admin: Pata orders zote.
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Helper method - convert Order → OrderResponse.
     */
    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .buyerId(order.getBuyer().getId())
                .buyerName(order.getBuyer().getFullName())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .shippingAddress(order.getShippingAddress())
                .items(items)
                .createdAt(order.getCreatedAt())
                .build();
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProductName())
                .sellerId(item.getSeller().getId())
                .sellerName(item.getSeller().getFullName())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .subtotal(item.getSubtotal())
                .build();
    }
}