package com.boudissa.saasapp.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {
    @NotBlank(message = "product name is required")
    @Size(min = 3, max = 255, message = "product name must be between 3 and 255 characters")
    private String name;

    @NotBlank(message = "product reference is required")
    @Size(min = 3, max = 255, message = "product reference must be between 3 and 255 characters")
    private String reference;

    private String description;

    @Positive(message = "alert threshold must be positive or zero")
    private Integer alertThreshold;

    @Positive(message = "price must be positive or zero")
    private BigDecimal price;

    @NotBlank(message = "category id is required")
    private String categoryId;
}
