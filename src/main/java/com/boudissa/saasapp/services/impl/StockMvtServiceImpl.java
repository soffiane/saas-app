package com.boudissa.saasapp.services.impl;

import com.boudissa.saasapp.dto.mapper.StockMvtMapper;
import com.boudissa.saasapp.dto.stockmvt.StockMvtRequest;
import com.boudissa.saasapp.dto.stockmvt.StockMvtResponse;
import com.boudissa.saasapp.entities.StockMvt;
import com.boudissa.saasapp.exception.ResourcesNotFoundException;
import com.boudissa.saasapp.repositories.ProductRepository;
import com.boudissa.saasapp.repositories.StockMvtRepository;
import com.boudissa.saasapp.services.StockMvtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockMvtServiceImpl implements StockMvtService {

    private final StockMvtRepository stockMvtRepository;
    private final StockMvtMapper stockMvtMapper;
    private final ProductRepository productRepository;

    @Override
    public void create(StockMvtRequest request) {
        checkIfProductExistsById(request.getProductId());
        StockMvt entity = stockMvtMapper.toEntity(request);
        entity.setMvtDate(LocalDateTime.now());
        stockMvtRepository.save(entity);
    }

    @Override
    public void update(String id, StockMvtRequest request) {
        findStockMovementById(id);
        checkIfProductExistsById(request.getProductId());
        final StockMvt stockMvt = stockMvtMapper.toEntity(request);
        stockMvt.setId(id);
        stockMvt.setMvtDate(LocalDateTime.now());
        stockMvtRepository.save(stockMvt);
    }

    @Override
    public void delete(String id) {
        stockMvtRepository.delete(findStockMovementById(id));
    }

    @Override
    public Page<StockMvtResponse> findAll(int page, int size) {
        return stockMvtRepository.findAll(PageRequest.of(page, size))
                .map(stockMvtMapper::toResponse);
    }

    @Override
    public StockMvtResponse findById(String id) {
        return stockMvtMapper.toResponse(findStockMovementById(id));
    }

    private void checkIfProductExistsById(String id) {
        if (!productRepository.existsById(id)) {
            log.error("Product not found");
            throw new ResourcesNotFoundException("Product not found");
        }
    }

    private StockMvt findStockMovementById(String id) {
        return stockMvtRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException("Stock movement not found"));
    }

    @Override
    public Page<StockMvtResponse> findAllByProductId(String productId, Pageable pageable) {
        return stockMvtRepository.findAllByProductId(productId, pageable)
                .map(stockMvtMapper::toResponse);
    }
}
