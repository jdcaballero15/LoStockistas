package com.stockistas.stockistas2025.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ArticuloProveedorResponseDTO {
    private Integer codArticulo;
    private String nombreArticulo;
    private boolean esPredeterminado;
}