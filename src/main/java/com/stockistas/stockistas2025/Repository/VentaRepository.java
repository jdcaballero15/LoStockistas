package com.stockistas.stockistas2025.Repository;

import com.stockistas.stockistas2025.Entity.Articulo;
import com.stockistas.stockistas2025.Entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta,Integer> {

    List<Venta> findByArticulo(Articulo articulo);
}
