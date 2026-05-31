package com.boudissa.saasapp.services;

import com.boudissa.saasapp.dto.stockmvt.StockMvtRequest;
import com.boudissa.saasapp.dto.stockmvt.StockMvtResponse;
import org.springframework.stereotype.Service;

@Service
public interface StockMvtService extends BasicService<StockMvtRequest, StockMvtResponse> {
}
