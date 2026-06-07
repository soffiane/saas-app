package com.boudissa.saasapp.controller;

import com.boudissa.saasapp.dto.stockmvt.StockMvtRequest;
import com.boudissa.saasapp.dto.stockmvt.StockMvtResponse;
import com.boudissa.saasapp.services.StockMvtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stock-mvts")
@RequiredArgsConstructor
public class StockMvtController {
    private final StockMvtService stockMvtService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid StockMvtRequest request) {
        stockMvtService.create(request);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@RequestBody @Valid StockMvtRequest request, @PathVariable @NotNull(message = "id is required") String id) {
        stockMvtService.update(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        stockMvtService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<StockMvtResponse>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(stockMvtService.findAll(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockMvtResponse> findById(@PathVariable @NotNull(message = "id is required") String id) {
        return ResponseEntity.ok(stockMvtService.findById(id));
    }
}
