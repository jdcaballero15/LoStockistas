package com.stockistas.stockistas2025.Repository;

import com.stockistas.stockistas2025.Entity.DetalleOrdenCompra;
import com.stockistas.stockistas2025.Entity.ArticuloProveedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetalleOrdenCompraRepository extends JpaRepository<DetalleOrdenCompra, Integer> {

    boolean existsByArticuloProveedorAndOrdenCompra_Estado_CodEstadoOCIn(
            ArticuloProveedor articuloProveedor,
            List<Integer> codEstados);
}