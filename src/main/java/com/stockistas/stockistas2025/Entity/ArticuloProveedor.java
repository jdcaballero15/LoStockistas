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

}
