package com.stockistas.stockistas2025.Repository;

import com.stockistas.stockistas2025.Entity.OrdenCompra;
import com.stockistas.stockistas2025.Entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Integer> {

    boolean existsByProveedorAndEstado_CodEstadoOCIn(Proveedor proveedor, List<Integer> codigosEstado);

}
