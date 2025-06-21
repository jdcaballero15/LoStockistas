package com.stockistas.stockistas2025.Dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VentaDTO {
    private Integer codArticulo;
    private Integer cantidadVendida;
}
