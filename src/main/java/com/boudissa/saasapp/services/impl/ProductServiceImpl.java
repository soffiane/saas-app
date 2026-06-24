package com.boudissa.saasapp.services.impl;

import com.boudissa.saasapp.dto.mapper.ProductMapper;
import com.boudissa.saasapp.dto.product.ProductRequest;
import com.boudissa.saasapp.dto.product.ProductResponse;
import com.boudissa.saasapp.entities.Product;
import com.boudissa.saasapp.exception.DuplicateResourceException;
import com.boudissa.saasapp.exception.ResourcesNotFoundException;
import com.boudissa.saasapp.repositories.CategoryRepository;
import com.boudissa.saasapp.repositories.ProductRepository;
import com.boudissa.saasapp.repositories.StockMvtRepository;
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
    private final StockMvtRepository stockMvtRepository;

    @Override
    public void create(ProductRequest request) {
        checkIfProductAlreadyExistsByReference(request);
        checkIfCategoryExistsById(request);
        final Product product = productMapper.toEntity(request);
        productRepository.save(product);
    }

    @Override
    public void update(String id, ProductRequest request) {
        final Product existingProduct = findProductById(id);
        checkIfProductAlreadyExistsByReferenceForUpdate(request, existingProduct.getReference());
        checkIfCategoryExistsById(request);
        final Product product = productMapper.toEntity(request);
        product.setId(id);
        productRepository.save(product);
    }

    @Override
    public void delete(String id) {
        productRepository.delete(findProductById(id));
    }

    @Override
    public Page<ProductResponse> findAll(int page, int size) {
        return productRepository.findAll(PageRequest.of(page, size))
                .map(product -> productMapper.toResponse(product, stockMvtRepository.sumQuantityByProductId(product.getId())));
    }

    @Override
    public ProductResponse findById(String id) {
        final Product product = findProductById(id);
        return productMapper.toResponse(product, stockMvtRepository.sumQuantityByProductId(product.getId()));
    }

    private void checkIfProductAlreadyExistsByReference(ProductRequest request) {
        final Optional<Product> product = productRepository.findByReference(request.getReference());
        if (product.isPresent()) {
            log.error("Product already exists");
            throw new DuplicateResourceException("Product already exists");
        }
    }

    private void checkIfProductAlreadyExistsByReferenceForUpdate(ProductRequest request, String currentReference) {
        if (!request.getReference().equals(currentReference)) {
            checkIfProductAlreadyExistsByReference(request);
        }
    }

    private void checkIfCategoryExistsById(ProductRequest request) {
        if (!categoryRepository.existsById(request.getCategoryId())) {
            log.error("Category not found");
            throw new ResourcesNotFoundException("Category not found");
        }
    }

    private Product findProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("Product not found"));
    }
}
