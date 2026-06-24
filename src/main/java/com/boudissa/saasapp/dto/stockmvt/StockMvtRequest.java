package com.boudissa.saasapp.dto.stockmvt;

import com.boudissa.saasapp.entities.TypeMvt;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockMvtRequest {

    @NotNull(message = "type mvt is required")
    private TypeMvt typeMvt;

    @Positive(message = "quantity must be positive")
    private Integer quantity;
    private String comment;

    @NotBlank(message = "product id is required")
    private String productId;

    @NotNull(message = "mvt date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @PastOrPresent(message = "mvt date must be in the past or present")
    private LocalDateTime mvtDate;
}
