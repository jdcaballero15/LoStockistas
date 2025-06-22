package com.stockistas.stockistas2025.Repository;

import com.stockistas.stockistas2025.Entity.Articulo;
import com.stockistas.stockistas2025.Entity.ModeloInventario;
import com.stockistas.stockistas2025.Entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
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
}