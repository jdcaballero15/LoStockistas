package com.stockistas.stockistas2025.Entity;

import jakarta.persistence.*;
import lombok.*;

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

}
