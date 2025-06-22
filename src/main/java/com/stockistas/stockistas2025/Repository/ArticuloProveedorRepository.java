package com.stockistas.stockistas2025.Repository;

import com.stockistas.stockistas2025.Entity.Articulo;
import com.stockistas.stockistas2025.Entity.ArticuloProveedor;
import com.stockistas.stockistas2025.Entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticuloProveedorRepository extends JpaRepository<ArticuloProveedor, Integer> {

    //-----------------------------------------------------------------------------------------------
    //Busco por articulo y proveedor
    Optional<ArticuloProveedor> findByArticuloAndProveedor(Articulo articulo, Proveedor proveedor);
    //-----------------------------------------------------------------------------------------------
    //Busco por proveedor
    List<ArticuloProveedor> findByProveedor(Proveedor proveedor);
    //-----------------------------------------------------------------------------------------------
    //Busco por artículo
    List<ArticuloProveedor> findByArticulo(Articulo articulo);
    //-----------------------------------------------------------------------------------------------

}
