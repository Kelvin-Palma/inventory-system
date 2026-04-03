package com.kelvin.inventory.services;

import com.kelvin.inventory.models.Category;
import com.kelvin.inventory.repositories.CategoryRepository;
import com.kelvin.inventory.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepo;
    private final ProductRepository productRepo;

    public List<Category> getAll() {
        return categoryRepo.findAll();
    }

    public Category getById(Long id) {
        return categoryRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found: " + id));
    }

    public Category create(Category data) {
        Category entity = Category.builder()
            .name(data.getName().trim())
            .description(data.getDescription() != null ? data.getDescription().trim() : null)
            .build();
        return categoryRepo.save(entity);
    }

    public Category update(Long id, Category data) {
        Category existing = getById(id);
        existing.setName(data.getName().trim());
        existing.setDescription(data.getDescription() != null ? data.getDescription().trim() : null);
        return categoryRepo.save(existing);
    }

    public void delete(Long id) {
        getById(id);
        if (productRepo.countByCategoryId(id) > 0) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "La categoría tiene productos asociados; reasígnalos o elimínalos primero."
            );
        }
        categoryRepo.deleteById(id);
    }
}
