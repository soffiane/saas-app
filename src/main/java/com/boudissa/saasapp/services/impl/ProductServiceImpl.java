package com.boudissa.saasapp.services.impl;

import com.boudissa.saasapp.dto.mapper.ProductMapper;
import com.boudissa.saasapp.dto.product.ProductRequest;
import com.boudissa.saasapp.dto.product.ProductResponse;
import com.boudissa.saasapp.entities.Category;
import com.boudissa.saasapp.entities.Product;
import com.boudissa.saasapp.repositories.CategoryRepository;
import com.boudissa.saasapp.repositories.ProductRepository;
import com.boudissa.saasapp.services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    @Override
    public void create(ProductRequest request) {
        checkIfProductAlreadyExistsByReference(request);
        checkIfCategoryExistsById(request);
        final Product product = productMapper.toEntity(request);
        productRepository.save(product);
    }

    @Override
    public void update(String id, ProductRequest request) {
        final Optional<Product> productExists = productRepository.findById(id);
        if(productExists.isEmpty()){
            log.error("Product not found");
            throw new IllegalArgumentException("Product not found");
        }
        checkIfProductAlreadyExistsByReference(request);
        checkIfCategoryExistsById(request);
        final Product product = productMapper.toEntity(request);
        productRepository.save(product);
    }

    @Override
    public void delete(String id) {

    }

    @Override
    public Page<ProductResponse> findAll(int page, int size) {
        return productRepository.findAll(PageRequest.of(page, size))
                .map(productMapper::toResponse);
    }

    @Override
    public ProductResponse findById(String id) {
        return productMapper.toResponse(productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found")));
    }

    private void checkIfProductAlreadyExistsByReference(ProductRequest request){
        final Optional<Product> product = productRepository.findByReference(request.getReference());
        if(product.isPresent()){
            log.error("Product already exists");
            throw new IllegalArgumentException("Product already exists");
        }
    }

    private void checkIfCategoryExistsById(ProductRequest request){
        final Optional<Category> category = categoryRepository.findById(request.getCategoryId());
        if(category.isEmpty()){
            log.error("Category not found");
            throw new IllegalArgumentException("Category not found");
        }
    }
}
