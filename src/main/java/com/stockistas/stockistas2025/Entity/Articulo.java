package com.stockistas.stockistas2025.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    private BigDecimal costoAlmacenamiento;  // (loteOptimo/2)* [i(lo definimos nosotros) * precioUnitario]
    //private BigDecimal costoPedido;  //CALCULAR (demandaAnual/loteOptimo)* costoDeHacerUnPedido( "S" PREGUNTAR DE DONDE LO SACAMOS)
    //private BigDecimal costoCompra; //CALCULAR  demandaAnual*precioUnitario(de ArtProveedor del Prov PRED)
    private Integer stockActual;
    private LocalDateTime fechaHoraBajaArticulo;
    private BigDecimal CGI;  //CALCULAR    (demandaAnual * costoCompra) + costoPedido + costoAlmacenamiento
    private Integer loteOptimo;  //CALCULAR   (2*demandaAnual* costoDeHacerUnPedido"S"/[i(lo definimos nosotros) * precioUnitario])^(1/2)
    private Integer puntoPedido;  //CALCULAR  (demandaAnual/365)* demoraEntrega (de ArtProveedor del Prov PRED)
    private Integer inventarioMax;
    private Integer stockSeguridad; // z (nivel de servicio)* desviacionEstandar     (me ingresan el z en porcentaje y una vez en el back tengo qeu transformarlo a z (la inversa de la distribucion normal) y luego multiplico por la desviacion eestandar ingresada
    private String urlImagen;
    private Double nivelServicio;
    private Double desviacionEstandar;
    private LocalDateTime fechaUltimoPedido;

    @Builder.Default
    private BigDecimal i = BigDecimal.valueOf(0.01);

    @ManyToOne
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedorPredeterminado;

    @OneToMany(mappedBy = "articulo", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ArticuloProveedor> relacionesConProveedores;

    //@OneToMany(mappedBy = "articulo", cascade = CascadeType.ALL)
    //@JsonBackReference
    //private List<Venta> ventas;

    @Enumerated(EnumType.STRING) // Guarda como 'LOTEFIJO' o 'INTERVALOFIJO' en la DB
    @Column(nullable = false)
    private ModeloInventario modeloInventario;
}
