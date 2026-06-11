package com.boudissa.saasapp.services.impl;

import com.boudissa.saasapp.dto.category.CategoryRequest;
import com.boudissa.saasapp.dto.category.CategoryResponse;
import com.boudissa.saasapp.dto.mapper.CategoryMapper;
import com.boudissa.saasapp.entities.Category;
import com.boudissa.saasapp.exception.DuplicateResourceException;
import com.boudissa.saasapp.exception.ResourcesNotFoundException;
import com.boudissa.saasapp.repositories.CategoryRepository;
import com.boudissa.saasapp.repositories.ProductRepository;
import com.boudissa.saasapp.services.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductRepository productRepository;

    @Override
    public void create(CategoryRequest request) {
        checkIfCategoryAlreadyExistsByName(request);
        final Category category = categoryMapper.toEntity(request);
        categoryRepository.save(category);
    }

    @Override
    public void update(String id, CategoryRequest request) {
        final Category existingCategory = findCategoryById(id);
        checkIfCategoryAlreadyExistsByNameForUpdate(request, existingCategory.getName());

        Category entity = categoryMapper.toEntity(request);
        entity.setId(id);
        categoryRepository.save(entity);
    }

    @Override
    public void delete(String id) {
        final Category category = findCategoryById(id);
        categoryRepository.delete(category);
    }

    @Override
    public Page<CategoryResponse> findAll(int page, int size) {
        return categoryRepository.findAll(PageRequest.of(page, size))
                .map(category -> categoryMapper.toResponse(category, productRepository.countByCategoryId(category.getId())));
    }

    @Override
    public CategoryResponse findById(String id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("Category not found"));
        return categoryMapper.toResponse(category, productRepository.countByCategoryId(category.getId()));
    }

    private void checkIfCategoryAlreadyExistsByName(CategoryRequest request) {
        final Optional<Category> categoryOptional = categoryRepository.findByNameIgnoreCase(request.getName());
        if (categoryOptional.isPresent()) {
            log.error("Category already exists");
            throw new DuplicateResourceException("Category already exists");
        }
    }

    private void checkIfCategoryAlreadyExistsByNameForUpdate(CategoryRequest request, String currentName) {
        if (!request.getName().equalsIgnoreCase(currentName)) {
            checkIfCategoryAlreadyExistsByName(request);
        }
    }

    private Category findCategoryById(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("Category not found"));
    }
}
