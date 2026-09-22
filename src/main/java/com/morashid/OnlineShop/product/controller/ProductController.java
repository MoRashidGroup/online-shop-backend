package com.morashid.OnlineShop.product.controller;

import com.morashid.OnlineShop.auth.security.CustomUserDetails;
import com.morashid.OnlineShop.common.ApiResponse;
import com.morashid.OnlineShop.product.dto.ProductRequest;
import com.morashid.OnlineShop.product.dto.ProductResponse;
import com.morashid.OnlineShop.product.service.ProductService;
import com.morashid.OnlineShop.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ProductController - REST endpoints za products.
 * 
 * PUBLIC (mtu yeyote):
 *   - GET /api/products
 *   - GET /api/products/{id}
 *   - GET /api/products/search?keyword=...
 *   - GET /api/products/category/{categoryId}
 * 
 * SELLER only:
 *   - POST   /api/seller/products
 *   - PUT    /api/seller/products/{id}
 *   - DELETE /api/seller/products/{id}
 *   - GET    /api/seller/products
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ============================================
    // PUBLIC ENDPOINTS
    // ============================================

    /**
     * GET /api/products
     * Pata products zote.
     */
    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success("Products retrieved", products));
    }

    /**
     * GET /api/products/{id}
     * Pata product moja.
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success("Product retrieved", product));
    }

    /**
     * GET /api/products/search?keyword=phone
     * Search products.
     */
    @GetMapping("/products/search")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> searchProducts(
            @RequestParam(required = false, defaultValue = "") String keyword) {

        List<ProductResponse> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(ApiResponse.success("Search results", products));
    }

    /**
     * GET /api/products/category/{categoryId}
     * Filter products kwa category.
     */
    @GetMapping("/products/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByCategory(
            @PathVariable Long categoryId) {

        List<ProductResponse> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success("Products by category", products));
    }

    // ============================================
    // SELLER ENDPOINTS
    // ============================================

    /**
     * POST /api/seller/products
     * Unda product mpya (SELLER only).
     * 
     * Kumbuka: Seller atatoka kwenye JWT token (via @AuthenticationPrincipal).
     * HATUTUMII sellerId kutoka request body!
     */
    @PostMapping("/seller/products")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User seller = userDetails.getUser();
        ProductResponse product = productService.createProduct(request, seller);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created", product));
    }

    /**
     * PUT /api/seller/products/{id}
     * Update product yako (SELLER only).
     */
    @PutMapping("/seller/products/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User seller = userDetails.getUser();
        ProductResponse product = productService.updateProduct(id, request, seller);

        return ResponseEntity.ok(ApiResponse.success("Product updated", product));
    }

    /**
     * DELETE /api/seller/products/{id}
     * Futa product yako (SELLER only).
     */
    @DeleteMapping("/seller/products/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User seller = userDetails.getUser();
        productService.deleteProduct(id, seller);

        return ResponseEntity.ok(ApiResponse.success("Product deleted"));
    }

    /**
     * GET /api/seller/products
     * Pata products zako (SELLER only).
     */
    @GetMapping("/seller/products")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getMyProducts(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User seller = userDetails.getUser();
        List<ProductResponse> products = productService.getMyProducts(seller);

        return ResponseEntity.ok(ApiResponse.success("My products", products));
    }
}