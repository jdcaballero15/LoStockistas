package com.stockistas.stockistas2025.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codVenta;

    private Integer cantProducto;
    private LocalDateTime fechaVenta;

    @ManyToOne
    @JoinColumn(name = "articulo_Id")
    private Articulo articulo;
}
