package com.boudissa.saasapp.repositories;

import com.boudissa.saasapp.entities.StockMvt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockMvtRepository extends JpaRepository<StockMvt, String> {
}
