package com.morashid.OnlineShop.category.repository;

import com.morashid.OnlineShop.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Tafuta category kwa jina.
     * 
     * SQL: SELECT * FROM categories WHERE name = ?
     */
    Optional<Category> findByName(String name);

    /**
     * Angalia kama category ipo kwa jina.
     * 
     * SQL: SELECT COUNT(*) > 0 FROM categories WHERE name = ?
     */
    boolean existsByName(String name);

    /**
     * Angalia kama category ipo kwa jina, isipokuwa moja yenye ID fulani.
     * 
     * Inatumika kwa UPDATE - kuhakikisha hatuna duplicate names.
     * 
     * SQL: SELECT COUNT(*) > 0 FROM categories 
     *      WHERE name = ? AND id != ?
     */
    boolean existsByNameAndIdNot(String name, Long id);
}