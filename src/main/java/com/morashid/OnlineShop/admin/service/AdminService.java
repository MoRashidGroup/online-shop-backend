package com.morashid.OnlineShop.admin.service;

import com.morashid.OnlineShop.admin.dto.DashboardStats;
import com.morashid.OnlineShop.category.repository.CategoryRepository;
import com.morashid.OnlineShop.exception.BadRequestException;
import com.morashid.OnlineShop.exception.ResourceNotFoundException;
import com.morashid.OnlineShop.order.entity.Order;
import com.morashid.OnlineShop.order.entity.OrderStatus;
import com.morashid.OnlineShop.order.repository.OrderRepository;
import com.morashid.OnlineShop.payment.entity.Payment;
import com.morashid.OnlineShop.payment.entity.PaymentStatus;
import com.morashid.OnlineShop.payment.repository.PaymentRepository;
import com.morashid.OnlineShop.product.repository.ProductRepository;
import com.morashid.OnlineShop.user.dto.UserResponse;
import com.morashid.OnlineShop.user.entity.Role;
import com.morashid.OnlineShop.user.entity.User;
import com.morashid.OnlineShop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.morashid.OnlineShop.admin.dto.CreateUserRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AdminService - business logic ya admin operations.
 */
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PasswordEncoder passwordEncoder;

    // ============================================
    // USER MANAGEMENT
    // ============================================

    /**
     * Pata users wote.
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Pata users kwa role.
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(Role role) {
        return userRepository.findByRoleOrderByCreatedAtDesc(role)
                .stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Pata user kwa ID.
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return toUserResponse(user);
    }

    /**
     * Badilisha role ya user.
     * 
     * MUHIMU: Admin hawezi ku-demote yeye mwenyewe!
     * (ili kuepuka kupoteza admin wa mwisho)
     */
    @Transactional
    public UserResponse updateUserRole(Long userId, Role newRole, User currentAdmin) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Prevent admin from changing their own role
        if (user.getId().equals(currentAdmin.getId())) {
            throw new BadRequestException("You cannot change your own role");
        }

        user.setRole(newRole);
        User updated = userRepository.save(user);
        return toUserResponse(updated);
    }

    /**
     * Enable/Disable user.
     */
    @Transactional
    public UserResponse updateUserStatus(Long userId, Boolean enabled, User currentAdmin) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Prevent admin from disabling themselves
        if (user.getId().equals(currentAdmin.getId())) {
            throw new BadRequestException("You cannot disable your own account");
        }

        user.setEnabled(enabled);
        User updated = userRepository.save(user);
        return toUserResponse(updated);
    }

    /**
     * Futa user (admin only).
     */
    @Transactional
    public void deleteUser(Long userId, User currentAdmin) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (user.getId().equals(currentAdmin.getId())) {
            throw new BadRequestException("You cannot delete your own account");
        }

        userRepository.delete(user);
    }

    // ============================================
    // REPORTS / STATISTICS
    // ============================================

    /**
     * Pata dashboard stats.
     */
    @Transactional(readOnly = true)
    public DashboardStats getDashboardStats() {

        long totalUsers = userRepository.count();
        long totalBuyers = userRepository.countByRole(Role.BUYER);
        long totalSellers = userRepository.countByRole(Role.SELLER);
        long totalAdmins = userRepository.countByRole(Role.ADMIN);
        long activeUsers = userRepository.countByEnabled(true);

        long totalProducts = productRepository.count();
        long totalCategories = categoryRepository.count();

        long totalOrders = orderRepository.count();
        long pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING).size();
        long completedOrders = orderRepository.findByStatus(OrderStatus.DELIVERED).size();

        // Total revenue kutoka payments zilizokamilika
        BigDecimal totalRevenue = paymentRepository.findByStatus(PaymentStatus.COMPLETED)
                .stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DashboardStats.builder()
                .totalUsers(totalUsers)
                .totalBuyers(totalBuyers)
                .totalSellers(totalSellers)
                .totalAdmins(totalAdmins)
                .activeUsers(activeUsers)
                .totalProducts(totalProducts)
                .totalCategories(totalCategories)
                .totalOrders(totalOrders)
                .pendingOrders(pendingOrders)
                .completedOrders(completedOrders)
                .totalRevenue(totalRevenue)
                .build();
    }

    /**
     * Helper method - User → UserResponse.
     */
    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNo(user.getPhoneNo())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }

            /**
         * Admin anaunda user mpya.
         * 
         * Tofauti na register ya kawaida:
         *   - Admin anaweza kuchagua role
         *   - Admin anaweza ku-set enabled/disabled
         *   - Hatumaanishi ni BUYER tu
         */
        @Transactional
        public UserResponse createUser(CreateUserRequest request) {

            // Check kama email ipo
            if (userRepository.existsByEmail(request.getEmail().toLowerCase())) {
                throw new BadRequestException("Email already registered: " + request.getEmail());
            }

            // Unda user mpya
            User user = User.builder()
                    .fullName(request.getFullName())
                    .email(request.getEmail().toLowerCase())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .phoneNo(request.getPhoneNo())
                    .role(request.getRole())
                    .enabled(request.getEnabled() != null ? request.getEnabled() : true)
                    .build();

            User saved = userRepository.save(user);
            return toUserResponse(saved);
        }
}