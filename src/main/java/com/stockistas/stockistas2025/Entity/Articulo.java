package com.stockistas.stockistas2025.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private Integer stockActual;
    private LocalDateTime fechaHoraBajaArticulo;
    private BigDecimal CGI;
    private Integer loteOptimo;
    private Integer puntoPedido;
    private Integer inventarioMax;
    private Integer stockSeguridad;
    private String urlImagen;
    private Double nivelServicio;
    private Double desviacionEstandar;
    private LocalDateTime fechaUltimoPedido;

    @Builder.Default
    private BigDecimal i = BigDecimal.valueOf(0.01);

    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedorPredeterminado;

    @OneToOne(mappedBy = "articulo", cascade = CascadeType.ALL)
    @JsonManagedReference
    private ArticuloProveedor relacionesConProveedores;

    @Enumerated(EnumType.STRING) // Guarda como 'LOTEFIJO' o 'INTERVALOFIJO' en la DB
    @Column(nullable = false)
    private ModeloInventario modeloInventario;
}
