package com.stockistas.stockistas2025.Repository;

import com.stockistas.stockistas2025.Entity.Articulo;
import com.stockistas.stockistas2025.Entity.ModeloInventario;
import com.stockistas.stockistas2025.Entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArticuloRepository extends JpaRepository<Articulo, Integer> {

    //-----------------------------------------------------------------------------------------------
    boolean existsByProveedorPredeterminado(Proveedor proveedor);
    //-----------------------------------------------------------------------------------------------
    List<Articulo> findByProveedorPredeterminadoIsNotNull();
    //-----------------------------------------------------------------------------------------------
    List<Articulo> findByModeloInventario(ModeloInventario modeloInventario);
    //-----------------------------------------------------------------------------------------------
    @Query("""
        SELECT DISTINCT a FROM Articulo a
        JOIN ArticuloProveedor ap ON ap.articulo = a
        JOIN DetalleOrdenCompra d ON d.articuloProveedor = ap
        JOIN OrdenCompra oc ON d MEMBER OF oc.detalles
        WHERE oc.estado.id NOT IN (1, 2)
    """)
    List<Articulo> findArticulosConOrdenesQueNoEstanPendientesNiEnviadas();
}