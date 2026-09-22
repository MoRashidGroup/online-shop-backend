package com.morashid.OnlineShop.product.service;

import com.morashid.OnlineShop.category.entity.Category;
import com.morashid.OnlineShop.category.repository.CategoryRepository;
import com.morashid.OnlineShop.exception.BadRequestException;
import com.morashid.OnlineShop.exception.ResourceNotFoundException;
import com.morashid.OnlineShop.product.dto.ProductRequest;
import com.morashid.OnlineShop.product.dto.ProductResponse;
import com.morashid.OnlineShop.product.entity.Product;
import com.morashid.OnlineShop.product.repository.ProductRepository;
import com.morashid.OnlineShop.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ProductService - business logic ya products.
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    // ============================================
    // PUBLIC METHODS (mtu yeyote)
    // ============================================

    /**
     * Pata products zote.
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Pata product moja kwa ID.
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return toResponse(product);
    }

    /**
     * Search products kwa keyword.
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.searchByKeyword(keyword)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Filter products kwa category.
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(Long categoryId) {
        // Check category ipo
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category", "id", categoryId);
        }

        return productRepository.findByCategoryId(categoryId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ============================================
    // SELLER METHODS
    // ============================================

    /**
     * Pata products za seller aliye-login.
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getMyProducts(User seller) {
        return productRepository.findBySellerId(seller.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Unda product mpya (SELLER).
     * 
     * Rules:
     *   - Category lazima iwe existing
     *   - Seller atatoka kwenye JWT (si request body!)
     * 
     * MUHIMU: Hatu-trust sellerId kutoka frontend! Tunatumia User aliye-login.
     */
    @Transactional
    public ProductResponse createProduct(ProductRequest request, User seller) {
        // Check category ipo
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category", "id", request.getCategoryId()));

        // Unda product
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .imageUrls(request.getImageUrls() != null 
                    ? request.getImageUrls() 
                    : new java.util.ArrayList<>())  // ← Badilisha kutoka imageUrl
                .category(category)
                .seller(seller)  // ← Kutoka JWT, si request!
                .build();

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    /**
     * Update product (SELLER).
     * 
     * MUHIMU: Seller anaweza ku-update products ZAKE pekee!
     */
    @Transactional
    public ProductResponse updateProduct(Long productId, ProductRequest request, User seller) {
        // Tafuta product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // Check product ni ya seller huyu
        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new BadRequestException("You can only update your own products");
        }

        // Check category ipo
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category", "id", request.getCategoryId()));

        // Update fields
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setImageUrls(request.getImageUrls() != null 
            ? request.getImageUrls() 
            : new java.util.ArrayList<>());  // ← Badilisha kutoka imageUrl
        product.setCategory(category);
        // Seller HAIbadiliki!

        Product updated = productRepository.save(product);
        return toResponse(updated);
    }

    /**
     * Futa product (SELLER).
     * 
     * MUHIMU: Seller anaweza kufuta products ZAKE pekee!
     */
    @Transactional
    public void deleteProduct(Long productId, User seller) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (!product.getSeller().getId().equals(seller.getId())) {
            throw new BadRequestException("You can only delete your own products");
        }

        productRepository.delete(product);
    }

    /**
     * Helper method - convert Product entity → ProductResponse DTO.
     */
    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imageUrls(product.getImageUrls())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .sellerId(product.getSeller().getId())
                .sellerName(product.getSeller().getFullName())
                .createdAt(product.getCreatedAt())
                .build();
    }
}