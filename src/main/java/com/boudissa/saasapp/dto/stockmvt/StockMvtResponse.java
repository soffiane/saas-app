package com.boudissa.saasapp.dto.stockmvt;

import com.boudissa.saasapp.entities.TypeMvt;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockMvtResponse {
    private TypeMvt typeMvt;
    private Integer quantity;
    private String comment;
    private String productId;
}
