package com.morashid.OnlineShop.category.controller;

import com.morashid.OnlineShop.category.dto.CategoryRequest;
import com.morashid.OnlineShop.category.dto.CategoryResponse;
import com.morashid.OnlineShop.category.service.CategoryService;
import com.morashid.OnlineShop.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CategoryController - REST endpoints za categories.
 * 
 * PUBLIC endpoints (GET):
 *   - GET /api/categories
 *   - GET /api/categories/{id}
 * 
 * ADMIN endpoints:
 *   - POST   /api/admin/categories
 *   - PUT    /api/admin/categories/{id}
 *   - DELETE /api/admin/categories/{id}
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // ============================================
    // PUBLIC ENDPOINTS
    // ============================================

    /**
     * GET /api/categories
     * Pata categories zote.
     */
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success("Categories retrieved", categories));
    }

    /**
     * GET /api/categories/{id}
     * Pata category moja.
     */
    @GetMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        CategoryResponse category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponse.success("Category retrieved", category));
    }

    // ============================================
    // ADMIN ENDPOINTS
    // ============================================

    /**
     * POST /api/admin/categories
     * Unda category mpya (ADMIN only).
     * 
     * @PreAuthorize("hasRole('ADMIN')") - ina-check role kabla ya ku-run.
     * 
     * Kumbuka: @EnableMethodSecurity ipo kwenye SecurityConfig.
     */
    @PostMapping("/admin/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryRequest request) {

        CategoryResponse category = categoryService.createCategory(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created", category));
    }

    /**
     * PUT /api/admin/categories/{id}
     * Update category (ADMIN only).
     */
    @PutMapping("/admin/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {

        CategoryResponse category = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponse.success("Category updated", category));
    }

    /**
     * DELETE /api/admin/categories/{id}
     * Futa category (ADMIN only).
     */
    @DeleteMapping("/admin/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted"));
    }
}