package com.stockistas.stockistas2025.Dto;

import lombok.Data;

import java.util.List;

@Data
public class ProveedorDTO {
    private String nombre;
    private String correo;
    private String telefono;
    private List<ArticuloProveedorDTO> articulos;
}
