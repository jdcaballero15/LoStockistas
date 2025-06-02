package com.stockistas.stockistas2025.Repository;

import com.stockistas.stockistas2025.Entity.Articulo;
import com.stockistas.stockistas2025.Entity.Proveedor;
import com.stockistas.stockistas2025.Entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {

    //List<Venta> findByArticulo(Articulo articulo);

}
