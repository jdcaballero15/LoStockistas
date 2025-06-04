package com.stockistas.stockistas2025.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    //@OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL)
    //@JsonBackReference  // Aquí serializa la lista normalmente
    //private List<ArticuloProveedor> relacionesConArticulos;

    //@OneToMany(mappedBy = "proveedor")
    //@JsonBackReference
    //private List<OrdenCompra> ordenes;

}
