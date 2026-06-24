package com.boudissa.saasapp.dto.mapper;

import com.boudissa.saasapp.dto.product.ProductRequest;
import com.boudissa.saasapp.dto.product.ProductResponse;
import com.boudissa.saasapp.entities.Category;
import com.boudissa.saasapp.entities.Product;
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
                .deleted(false)
                .build();
    }

    /**
     * stockMovements est une relation @OneToMany, donc par défaut elle est chargée en lazy loading.
     * <p>
     * Quand le mapper essayait d'accéder à entity.getStockMovements(),
     * la session Hibernate était déjà fermée, donc Hibernate ne pouvait plus charger la collection.
     * Il faut donc passer le stockQuantity qui provient d'une autre requete
     * </p>
     */
    public ProductResponse toResponse(final Product entity, final int stockQuantity){
        return ProductResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .reference(entity.getReference())
                .description(entity.getDescription())
                .alertThreshold(entity.getAlertThreshold())
                .price(entity.getPrice())
                .categoryId(entity.getCategory().getId())
                .categoryName(entity.getCategory().getName())
                .stockQuantity(stockQuantity)
                .build();
    }
}
