package com.stockistas.stockistas2025.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticuloProveedorDTO {
    private Integer codArticulo;
    private BigDecimal precioUnitario;
    private BigDecimal cargosPedido;
    private Integer demoraEntregaDias;
    private ProveedorDTOOutput proveedorDTOOutput;
}
