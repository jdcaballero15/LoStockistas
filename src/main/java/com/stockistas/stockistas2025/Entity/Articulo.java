package com.stockistas.stockistas2025.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Articulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codArticulo;

    private String nombreArt;
    private String descripArt;
    private Integer demandaAnual;
    private BigDecimal costoAlmacenamiento;
    private BigDecimal costoPedido;
    private BigDecimal costoCompra;
    private Integer stockActual;
    private LocalDateTime fechaHoraBajaArticulo;
    private BigDecimal CGI;
    private Integer loteOptimo;
    private Integer puntoPedido;
    private Integer inventarioMax;
    private Integer stockSeguridadLF;
    private Integer stockSeguridadIF;

    @ManyToOne
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedorPredeterminado;

    @OneToMany(mappedBy = "articulo", cascade = CascadeType.ALL)
    private List<ArticuloProveedor> relacionesConProveedores;

    @OneToMany(mappedBy = "articulo", cascade = CascadeType.ALL)
    private List<Venta> ventas;

    @Enumerated(EnumType.STRING) // Guarda como 'LOTEFIJO' o 'INTERVALOFIJO' en la DB
    @Column(nullable = false)
    private ModeloInventario modeloInventario;
}

