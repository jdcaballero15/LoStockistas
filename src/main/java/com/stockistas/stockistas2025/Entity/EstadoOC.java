package com.stockistas.stockistas2025.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EstadoOC {

    @Id
    private Integer codEstadoOC;
    private String nombreEstadoOC;

    @OneToMany(mappedBy = "estado")
    @JsonBackReference
    private List<OrdenCompra> ordenes;
}
