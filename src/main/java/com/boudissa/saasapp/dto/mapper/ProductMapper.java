package com.boudissa.saasapp.dto.mapper;

import com.boudissa.saasapp.dto.product.ProductRequest;
import com.boudissa.saasapp.dto.product.ProductResponse;
import com.boudissa.saasapp.entities.Category;
import com.boudissa.saasapp.entities.Product;
import com.boudissa.saasapp.entities.StockMvt;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(final ProductRequest request){
        return Product.builder()
                .name(request.getName())
                .reference(request.getReference())
                .description(request.getDescription())
                .alertThreshold(request.getAlertThreshold())
                .price(request.getPrice())
                .category(Category.builder().id(request.getCategoryId()).build())
                .build();
    }

    public ProductResponse toResponse(final Product entity){
        return ProductResponse.builder()
                .name(entity.getName())
                .reference(entity.getReference())
                .description(entity.getDescription())
                .alertThreshold(entity.getAlertThreshold())
                .price(entity.getPrice())
                .categoryId(entity.getCategory().getId())
                .categoryName(entity.getCategory().getName())
                .stockQuantity(entity.getStockMovements().stream().mapToInt(StockMvt::getQuantity).sum())
                .build();
    }
}
