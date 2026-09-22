package com.morashid.OnlineShop.admin.controller;

import com.morashid.OnlineShop.admin.dto.DashboardStats;
import com.morashid.OnlineShop.admin.dto.UpdateUserRoleRequest;
import com.morashid.OnlineShop.admin.dto.UpdateUserStatusRequest;
import com.morashid.OnlineShop.admin.service.AdminService;
import com.morashid.OnlineShop.auth.security.CustomUserDetails;
import com.morashid.OnlineShop.common.ApiResponse;
import com.morashid.OnlineShop.user.dto.UserResponse;
import com.morashid.OnlineShop.user.entity.Role;
import com.morashid.OnlineShop.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.morashid.OnlineShop.admin.dto.CreateUserRequest;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * AdminController - REST endpoints za admin (ADMIN only).
 * 
 * Endpoints:
 *   - GET    /api/admin/users                → Users wote
 *   - GET    /api/admin/users/{id}           → User mmoja
 *   - GET    /api/admin/users/role/{role}    → Users kwa role
 *   - PUT    /api/admin/users/{id}/role      → Badilisha role
 *   - PUT    /api/admin/users/{id}/status    → Enable/Disable
 *   - DELETE /api/admin/users/{id}           → Futa user
 *   - GET    /api/admin/dashboard            → Stats
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    // ============================================
    // USER MANAGEMENT
    // ============================================

    /**
     * GET /api/admin/users
     * Pata users wote.
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = adminService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Users retrieved", users));
    }
        /**
     * POST /api/admin/users
     * Unda user mpya (ADMIN only).
     * 
     * Admin anaweza kuchagua role: ADMIN, SELLER, au BUYER.
     */
    @PostMapping("/users")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        UserResponse user = adminService.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created", user));
    }


    /**
     * GET /api/admin/users/{id}
     * Pata user mmoja.
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse user = adminService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("User retrieved", user));
    }

    /**
     * GET /api/admin/users/role/{role}
     * Pata users kwa role (ADMIN, SELLER, BUYER).
     */
    @GetMapping("/users/role/{role}")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByRole(
            @PathVariable Role role) {

        List<UserResponse> users = adminService.getUsersByRole(role);
        return ResponseEntity.ok(ApiResponse.success("Users by role", users));
    }

    /**
     * PUT /api/admin/users/{id}/role
     * Badilisha role ya user.
     * 
     * Kwa hii, admin anaweza ku-make buyer kuwa seller!
     */
    @PutMapping("/users/{id}/role")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRoleRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User currentAdmin = userDetails.getUser();
        UserResponse user = adminService.updateUserRole(id, request.getRole(), currentAdmin);
        return ResponseEntity.ok(ApiResponse.success("User role updated", user));
    }

    /**
     * PUT /api/admin/users/{id}/status
     * Enable/Disable user.
     */
    @PutMapping("/users/{id}/status")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User currentAdmin = userDetails.getUser();
        UserResponse user = adminService.updateUserStatus(id, request.getEnabled(), currentAdmin);
        return ResponseEntity.ok(ApiResponse.success("User status updated", user));
    }

    /**
     * DELETE /api/admin/users/{id}
     * Futa user.
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User currentAdmin = userDetails.getUser();
        adminService.deleteUser(id, currentAdmin);
        return ResponseEntity.ok(ApiResponse.success("User deleted"));
    }

    // ============================================
    // REPORTS
    // ============================================

    /**
     * GET /api/admin/dashboard
     * Pata dashboard stats.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardStats>> getDashboardStats() {
        DashboardStats stats = adminService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success("Dashboard stats", stats));
    }
}