package com.stockistas.stockistas2025.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdenCompraDTO {
    private Integer codArticulo;     // ID del artículo a comprar (obligatorio)
    private Integer cantidad;        // Cantidad a comprar (opcional)

}