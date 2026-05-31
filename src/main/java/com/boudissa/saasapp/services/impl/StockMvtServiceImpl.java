package com.boudissa.saasapp.services.impl;

import com.boudissa.saasapp.dto.mapper.StockMvtMapper;
import com.boudissa.saasapp.dto.stockmvt.StockMvtRequest;
import com.boudissa.saasapp.dto.stockmvt.StockMvtResponse;
import com.boudissa.saasapp.entities.Product;
import com.boudissa.saasapp.entities.StockMvt;
import com.boudissa.saasapp.repositories.ProductRepository;
import com.boudissa.saasapp.repositories.StockMvtRepository;
import com.boudissa.saasapp.services.StockMvtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        stockMvtRepository.save(stockMvtMapper.toEntity(request));
    }

    @Override
    public void update(String id, StockMvtRequest request) {
        Optional<StockMvt> stockMvt = stockMvtRepository.findById(id);
        if(stockMvt.isEmpty()) {
            log.error("Stock movement not found");
            throw new IllegalArgumentException("Stock movement not found");
        }
        checkIfProductExistsById(request.getProductId());
        stockMvtRepository.save(stockMvtMapper.toEntity(request));
    }

    @Override
    public void delete(String id) {
        final StockMvt stockMovementNotFound = stockMvtRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Stock movement not found"));
        stockMvtRepository.delete(stockMovementNotFound);
    }

    @Override
    public Page<StockMvtResponse> findAll(int page, int size) {
        return stockMvtRepository.findAll(PageRequest.of(page, size))
                .map(stockMvtMapper::toResponse);
    }

    @Override
    public StockMvtResponse findById(String id) {
        return stockMvtMapper.toResponse(stockMvtRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Stock movement not found")));
    }

    private void checkIfProductExistsById(String id) {
        final Optional<Product> product = productRepository.findById(id);
        if(product.isEmpty()){
            log.error("Product not found");
            throw new IllegalArgumentException("Product not found");
        }
    }

}
