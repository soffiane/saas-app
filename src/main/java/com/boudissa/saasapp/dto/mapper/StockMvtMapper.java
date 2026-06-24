package com.boudissa.saasapp.dto.mapper;

import com.boudissa.saasapp.dto.stockmvt.StockMvtRequest;
import com.boudissa.saasapp.dto.stockmvt.StockMvtResponse;
import com.boudissa.saasapp.entities.Product;
import com.boudissa.saasapp.entities.StockMvt;
import org.springframework.stereotype.Component;

@Component
public class StockMvtMapper {

    public StockMvt toEntity(final StockMvtRequest request){
        return StockMvt.builder()
                .mvtDate(request.getMvtDate())
                .typeMvt(request.getTypeMvt())
                .quantity(request.getQuantity())
                .comment(request.getComment())
                .product(Product.builder().id(request.getProductId()).build())
                .deleted(false)
                .build();
    }

    public StockMvtResponse toResponse(final StockMvt stockMvt){
        return StockMvtResponse.builder()
                .id(stockMvt.getId())
                .typeMvt(stockMvt.getTypeMvt())
                .quantity(stockMvt.getQuantity())
                .comment(stockMvt.getComment())
                .productId(stockMvt.getProduct().getId())
                .mvtDate(stockMvt.getMvtDate())
                .build();
    }
}
