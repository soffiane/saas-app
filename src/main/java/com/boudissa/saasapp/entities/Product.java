package com.boudissa.saasapp.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "products")
public class Product extends AbstractEntity {
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "reference", nullable = false, unique = true)
    private String reference;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Column(name = "alert_threshold", nullable = false)
    private Integer alertThreshold;
    @Column(name = "price", nullable = false)
    private BigDecimal price;
    //private Integer quantity;
}
