package com.boudissa.saasapp.dto.category;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponse {
    private String id;
    private String name;
    private String description;
    private int nbProducts;
}
