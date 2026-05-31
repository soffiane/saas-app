package com.boudissa.saasapp.services;

import com.boudissa.saasapp.dto.product.ProductRequest;
import com.boudissa.saasapp.dto.product.ProductResponse;
import org.springframework.stereotype.Service;

@Service
public interface ProductService extends BasicService<ProductRequest, ProductResponse> {
}
