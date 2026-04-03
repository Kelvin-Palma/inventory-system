package com.kelvin.inventory.services;

import com.kelvin.inventory.models.Product;
import com.kelvin.inventory.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepo;

    public List<Product> getAll() {
        return productRepo.findAll();
    }

    public Product getById(Long id) {
        return productRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    public Product create(Product product) {
        return productRepo.save(product);
    }

    public Product update(Long id, Product data) {
        Product existing = getById(id);
        existing.setName(data.getName());
        existing.setDescription(data.getDescription());
        existing.setPrice(data.getPrice());
        existing.setStock(data.getStock());
        existing.setMinStock(data.getMinStock());
        existing.setCategory(data.getCategory());
        return productRepo.save(existing);
    }

    public void delete(Long id) {
        productRepo.deleteById(id);
    }

    public List<Product> getLowStock() {
        return productRepo.findLowStockProducts();
    }

    public List<Product> getByCategory(Long catId) {
        return productRepo.findByCategoryId(catId);
    }

    // Report: amount y total price of products by category
    public Map<String, Object> getReportByCategory(Long catId) {
        List<Product> products = productRepo.findByCategoryId(catId);
        BigDecimal totalValue = products.stream()
            .map(p -> p.getPrice().multiply(BigDecimal.valueOf(p.getStock())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Map.of(
            "totalProducts", products.size(),
            "totalStockValue", totalValue,
            "products", products,
            "lowStockCount", products.stream()
                .filter(p -> p.getStock() <= p.getMinStock()).count()
        );
    }
}
