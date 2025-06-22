package com.stockistas.stockistas2025.Repository;

import com.stockistas.stockistas2025.Entity.ArticuloProveedor;
import com.stockistas.stockistas2025.Entity.OrdenCompra;
import com.stockistas.stockistas2025.Entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Integer> {

    //-----------------------------------------------------------------------------------------------
    // Verifica si existen órdenes de compra asociadas a un proveedor en alguno de los estados indicados
    boolean existsByProveedorAndEstado_CodEstadoOCIn(Proveedor proveedor, List<Integer> codigosEstado);

    //-----------------------------------------------------------------------------------------------
    // Busca todas las órdenes de compra que contengan un artículo con el código indicado
    List<OrdenCompra> findByDetalles_ArticuloProveedor_Articulo_CodArticulo(Integer codArticulo);

    //-----------------------------------------------------------------------------------------------
    // Verifica si existen órdenes de compra con un artículo-proveedor específico en alguno de los estados indicados
    boolean existsByDetalles_ArticuloProveedorAndEstado_CodEstadoOCIn(
            ArticuloProveedor articuloProveedor,
            List<Integer> codEstados);

    //-----------------------------------------------------------------------------------------------
    // Busca órdenes de compra para un artículo específico que se encuentren en alguno de los estados indicados
    List<OrdenCompra> findByDetalles_ArticuloProveedor_Articulo_CodArticuloAndEstado_CodEstadoOCIn(
            Integer codArticulo,
            List<Integer> codEstados);
    //-----------------------------------------------------------------------------------------------
}
