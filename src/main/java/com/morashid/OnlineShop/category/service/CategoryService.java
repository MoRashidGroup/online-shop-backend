package com.morashid.OnlineShop.category.service;

import com.morashid.OnlineShop.category.dto.CategoryRequest;
import com.morashid.OnlineShop.category.dto.CategoryResponse;
import com.morashid.OnlineShop.category.entity.Category;
import com.morashid.OnlineShop.category.repository.CategoryRepository;
import com.morashid.OnlineShop.exception.BadRequestException;
import com.morashid.OnlineShop.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * CategoryService - business logic ya categories.
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Pata categories zote.
     */
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Pata category moja kwa ID.
     */
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return toResponse(category);
    }

    /**
     * Unda category mpya.
     * 
     * Flow:
     *   1. Check kama name ipo tayari → BadRequestException
     *   2. Unda Category entity
     *   3. Save kwenye database
     *   4. Return CategoryResponse
     */
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        // Check kama name ipo tayari
        if (categoryRepository.existsByName(request.getName())) {
            throw new BadRequestException("Category already exists: " + request.getName());
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        Category saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    /**
     * Update category.
     * 
     * Flow:
     *   1. Tafuta category → ResourceNotFoundException kama haipo
     *   2. Kama name imebadilika, check kama name mpya inatumika na category nyingine
     *   3. Update fields
     *   4. Save
     */
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        // Tafuta category
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        // Check kama jina jipya linatumika na category nyingine
        if (!category.getName().equals(request.getName())
                && categoryRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new BadRequestException("Category name already in use: " + request.getName());
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category updated = categoryRepository.save(category);
        return toResponse(updated);
    }

    /**
     * Futa category.
     */
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        categoryRepository.delete(category);
    }

    /**
     * Helper method - convert Category entity → CategoryResponse DTO.
     */
    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .build();
    }
}