package com.boudissa.saasapp.dto.stockmvt;

import com.boudissa.saasapp.entities.TypeMvt;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockMvtRequest {
    private TypeMvt typeMvt;
    private Integer quantity;
    private String comment;
    private String productId;
    private LocalDateTime mvtDate;
}
