package com.stockistas.stockistas2025.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonBackReference  // No serializa para evitar recursión
    private Articulo articulo;

    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    @JsonBackReference  // No serializa para evitar recursión
    private Proveedor proveedor;

    //@OneToMany(mappedBy = "articuloProveedor", cascade = CascadeType.ALL, orphanRemoval = true)
    //@JsonBackReference
    //private List<DetalleOrdenCompra> detallesOC;
}
