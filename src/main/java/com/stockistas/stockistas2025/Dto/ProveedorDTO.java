package com.stockistas.stockistas2025.Dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProveedorDTO {
    private String nombre;
    private String correo;
    private String direccion;
    private String telefono;
    private Integer intervaloReposicion;
    private List<ArticuloProveedorDTO> articulos;
}
