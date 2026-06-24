package com.boudissa.saasapp.repositories;

import com.boudissa.saasapp.entities.StockMvt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMvtRepository extends JpaRepository<StockMvt, String> {

    @Query("SELECT COALESCE(SUM(sm.quantity), 0) FROM StockMvt sm WHERE sm.product.id = :productId")
    int sumQuantityByProductId(String productId);

    Page<StockMvt> findAllByProductId(String productId, Pageable pageable);
}
