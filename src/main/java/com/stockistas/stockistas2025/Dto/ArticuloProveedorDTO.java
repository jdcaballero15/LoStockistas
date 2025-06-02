package com.stockistas.stockistas2025.Dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ArticuloProveedorDTO {
    private Integer codArticulo;
    private BigDecimal precioUnitario;
    private BigDecimal cargosPedido;
    private Integer demoraEntregaDias;
}
