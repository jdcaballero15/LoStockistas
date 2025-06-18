package com.stockistas.stockistas2025.Dto;

import com.stockistas.stockistas2025.Entity.ModeloInventario;
import com.stockistas.stockistas2025.Entity.Proveedor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ArticuloDTO {
    private Integer codArticulo;
    private String nombreArt;
    private String descripArt;
    private Integer demandaAnual;
    private BigDecimal costoAlmacenamiento;
    private BigDecimal costoPedido;
    private Integer stockActual;
    private LocalDateTime fechaHoraBajaArticulo;
    private BigDecimal CGI;
    private Integer loteOptimo;
    private Integer puntoPedido;
    private Integer inventarioMax;
    private Integer stockSeguridad;
    private ModeloInventario modeloInventario;
    private Proveedor proveedorPredeterminado;
    private String urlImagen;
    private Double nivelServicio;
    private Double desviacionEstandar;
}


