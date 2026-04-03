package com.kelvin.inventory.repositories;

import com.kelvin.inventory.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Search by category
    List<Product> findByCategoryId(Long categoryId);

    long countByCategoryId(Long categoryId);

    // Products with low stock
    @Query("SELECT p FROM Product p WHERE p.stock <= p.minStock")
    List<Product> findLowStockProducts();

    // Search by name (contains)
    List<Product> findByNameContainingIgnoreCase(String name);
}