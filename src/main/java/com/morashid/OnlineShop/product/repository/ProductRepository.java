package com.morashid.OnlineShop.product.repository;

import com.morashid.OnlineShop.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Pata products zote za seller fulani.
     * 
     * SQL: SELECT * FROM products WHERE seller_id = ?
     */
    List<Product> findBySellerId(Long sellerId);

    /**
     * Pata products zote za category fulani.
     * 
     * SQL: SELECT * FROM products WHERE category_id = ?
     */
    List<Product> findByCategoryId(Long categoryId);

    /**
     * Search products kwa keyword kwenye name au description.
     * 
     * SQL: SELECT * FROM products 
     *      WHERE LOWER(name) LIKE LOWER(?%) 
     *         OR LOWER(description) LIKE LOWER(?%)
     * 
     * JPQL custom query - inaonyesha nguvu ya @Query.
     */
    @Query("SELECT p FROM Product p " +
           "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "   OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchByKeyword(@Param("keyword") String keyword);

    /**
     * Search products kwa keyword NA category.
     * 
     * SQL: SELECT * FROM products 
     *      WHERE category_id = ?
     *        AND (name LIKE ?% OR description LIKE ?%)
     */
    @Query("SELECT p FROM Product p " +
           "WHERE p.category.id = :categoryId " +
           "  AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "    OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Product> searchByKeywordAndCategory(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId);

    /**
     * Pata products zenye stock (stock > 0).
     * 
     * SQL: SELECT * FROM products WHERE stock_quantity > 0
     */
    List<Product> findByStockQuantityGreaterThan(Integer quantity);

    /**
     * Angalia kama category ina products.
     * 
     * Inatumika kabla ya kufuta category.
     */
    boolean existsByCategoryId(Long categoryId);

    /**
     * Paginated products - kwa performance kama products ni nyingi sana.
     */
    Page<Product> findAll(Pageable pageable);
}