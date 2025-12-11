package com.harnessdemo.repositories;

import com.harnessdemo.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(String category);

    List<Product> findByActive(boolean active);

    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<Product> findByStockGreaterThan(int minStock);

    List<Product> findByNameContainingIgnoreCase(String name);

    @Query("SELECT p FROM Product p WHERE p.stock <= ?1 AND p.active = true")
    List<Product> findLowStockProducts(int threshold);

    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.active = true")
    List<String> findActiveCategories();

    long countByCategory(String category);
}
