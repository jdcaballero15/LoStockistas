package com.stockistas.stockistas2025.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleOrdenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer numDetalleOC;

    private BigDecimal subTotal;

    @ManyToOne
    @JoinColumn(name = "articulo_proveedor_id", nullable = false)
    private ArticuloProveedor articuloProveedor;

}
