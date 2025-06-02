package com.stockistas.stockistas2025.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer demoraEntrega;
    private BigDecimal precioUnitario;
    private BigDecimal cargosPedido;

    @ManyToOne
    @JoinColumn(name = "articulo_id")
    private Articulo articulo;

    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    @OneToMany(mappedBy = "articuloProveedor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleOrdenCompra> detallesOC;
}