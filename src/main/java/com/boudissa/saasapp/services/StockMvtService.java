package com.boudissa.saasapp.services;

import com.boudissa.saasapp.dto.stockmvt.StockMvtRequest;
import com.boudissa.saasapp.dto.stockmvt.StockMvtResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StockMvtService extends BasicService<StockMvtRequest, StockMvtResponse> {
    Page<StockMvtResponse> findAllByProductId(String productId, Pageable pageable);
}
