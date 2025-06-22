package com.stockistas.stockistas2025.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorDTOOutput {
    private Integer codProveedor;
    private String nombreProveedor;
    private String direccionProveedor;
    private String telefonoProveedor;
    private String emailProveedor;
}
