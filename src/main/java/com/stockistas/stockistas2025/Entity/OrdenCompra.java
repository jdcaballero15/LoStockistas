package com.stockistas.stockistas2025.Entity;

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
public class OrdenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer numOC;

    private Integer cantArt;
    private BigDecimal montoCompra;

    @ManyToOne
    @JoinColumn(name = "estado_oc_id")
    @JsonManagedReference
    private EstadoOC estado;

    @ManyToOne
    @JoinColumn(name = "proveedor_id", nullable = false)
    @JsonManagedReference
    private Proveedor proveedor;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "orden_compra_id") // FK en la tabla detalle_orden_compra
    private List<DetalleOrdenCompra> detalles;
}

