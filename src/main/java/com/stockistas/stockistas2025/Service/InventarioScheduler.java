package com.stockistas.stockistas2025.Service;

import com.stockistas.stockistas2025.Entity.Articulo;
import com.stockistas.stockistas2025.Entity.ModeloInventario;
import com.stockistas.stockistas2025.Entity.OrdenCompra;
import com.stockistas.stockistas2025.Repository.ArticuloRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventarioScheduler {

    private final ArticuloRepository articuloRepository;
    private final OrdenCompraService ordenCompraService;

    public InventarioScheduler(ArticuloRepository articuloRepository,
                               OrdenCompraService ordenCompraService) {
        this.articuloRepository = articuloRepository;
        this.ordenCompraService = ordenCompraService;
    }

    // Se ejecuta cada 2min
    @Scheduled(cron = "0 */2 * * * *")
    public void verificarStockIntervaloFijo() {
        List<Articulo> articulos = articuloRepository.findByModeloInventario(ModeloInventario.INTERVALOFIJO);

        for (Articulo articulo : articulos) {
            if (articulo.getProveedorPredeterminado() != null) {

                Optional<OrdenCompra> oc = ordenCompraService.generarOrdenCompraSiCorresponde(articulo.getCodArticulo());
                oc.ifPresent(o -> {
                    System.out.println("Orden generada automáticamente para el artículo: " + articulo.getNombreArt());
                });
            }
        }
    }
}