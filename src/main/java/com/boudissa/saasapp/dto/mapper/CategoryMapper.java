package com.boudissa.saasapp.dto.mapper;

import com.boudissa.saasapp.dto.category.CategoryRequest;
import com.boudissa.saasapp.dto.category.CategoryResponse;
import com.boudissa.saasapp.entities.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(final CategoryRequest request) {
        return Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .deleted(false)
                .build();
    }

    /**
     * products est une relation @OneToMany, donc par défaut elle est chargée en lazy loading.
     * <p>
     * Quand le mapper essayait d’accéder à category.getProducts().size(),
     * la session Hibernate était déjà fermée, donc Hibernate ne pouvait plus charger la collection.
     * Il faut donc passer le nbProducts qui provient d'une autre requete
     *</p>
     */
    public CategoryResponse toResponse(final Category category, final int nbProducts) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .nbProducts(nbProducts)
                .build();
    }
}
