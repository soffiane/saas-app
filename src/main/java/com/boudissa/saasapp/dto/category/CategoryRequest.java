package com.boudissa.saasapp.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryRequest {

    @NotBlank(message = "category name is required")
    @Size(min = 3, max = 255, message = "category name must be between 3 and 255 characters")
    private String name;

    @Size(max = 500, message = "category description must be less than 500 characters")
    private String description;
}
