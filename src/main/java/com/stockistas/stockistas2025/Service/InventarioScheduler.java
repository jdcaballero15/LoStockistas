package com.stockistas.stockistas2025.Service;

import com.stockistas.stockistas2025.Entity.Articulo;
import com.stockistas.stockistas2025.Entity.ModeloInventario;
import com.stockistas.stockistas2025.Entity.OrdenCompra;
import com.stockistas.stockistas2025.Repository.ArticuloRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InventarioScheduler {

    private final ArticuloRepository articuloRepository;
    private final OrdenCompraService ordenCompraService;

    //-----------------------------------------------------------------------------------------------
    //Constructor del demonio (INTERVALO FIJO)
    public InventarioScheduler(ArticuloRepository articuloRepository,OrdenCompraService ordenCompraService) {
        this.articuloRepository = articuloRepository;
        this.ordenCompraService = ordenCompraService;
    }

    //-----------------------------------------------------------------------------------------------
    // Se ejecuta cada 2min
    @Scheduled(cron = "0 */1 * * * *")
    public void verificarStockIntervaloFijo() {
        System.out.println("Se esta ejecutando el daemon");
        //Busco todos los artículos con modelo intervalo fijo
        List<Articulo> articulos = articuloRepository.findByModeloInventario(ModeloInventario.INTERVALOFIJO);

        for (Articulo articulo : articulos) {
            //Solo trabaja el demonio con los articulos que tienen proveedor predeterminado asignado
            if (articulo.getProveedorPredeterminado() == null) continue;

            Integer intervaloDias = articulo.getProveedorPredeterminado().getIntervaloReposicion();
            LocalDateTime ultimaFecha = articulo.getFechaUltimoPedido();
            LocalDateTime ahora = LocalDateTime.now();

            // Verificar si debe generar orden de compra
            boolean debeGenerarOrden = false;

            if (ultimaFecha == null) {
                // Si nunca se hizo pedido, lo forzamos
                debeGenerarOrden = true;
            } else if (ultimaFecha.plusDays(intervaloDias).isBefore(ahora)) {
                // Si ha pasado el intervalo desde el último pedido
                debeGenerarOrden = true;
            }

            if (debeGenerarOrden) {
                Optional<OrdenCompra> oc = ordenCompraService.generarOrdenCompraSiCorresponde(articulo.getCodArticulo());
                if (oc.isPresent()) {
                    articulo.setFechaUltimoPedido(ahora);
                    articuloRepository.save(articulo);
                }
            }
        }
    }

    //-----------------------------------------------------------------------------------------------
}