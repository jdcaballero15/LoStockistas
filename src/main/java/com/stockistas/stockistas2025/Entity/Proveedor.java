package com.stockistas.stockistas2025.Entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codProveedor;

    private String nombreProveedor;
    private String direccionProveedor;
    private String telefonoProveedor;
    private String emailProveedor;
    private LocalDateTime fechaHoraBajaProveedor;
    private Integer intervaloReposicion;

}
