package com.boudissa.saasapp.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "stock_mvts")
public class StockMvt extends AbstractEntity {
    @Column(name = "type_mvt", nullable = false)
    @Enumerated(EnumType.STRING)
    private TypeMvt typeMvt;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "mvt_date", nullable = false)
    private LocalDateTime mvtDate;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
