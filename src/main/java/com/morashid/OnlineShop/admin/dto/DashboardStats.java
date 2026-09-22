package com.morashid.OnlineShop.admin.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO ya dashboard statistics kwa admin.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStats {

    // Users
    private long totalUsers;
    private long totalBuyers;
    private long totalSellers;
    private long totalAdmins;
    private long activeUsers;

    // Products
    private long totalProducts;
    private long totalCategories;

    // Orders
    private long totalOrders;
    private long pendingOrders;
    private long completedOrders;

    // Revenue
    private BigDecimal totalRevenue;
}