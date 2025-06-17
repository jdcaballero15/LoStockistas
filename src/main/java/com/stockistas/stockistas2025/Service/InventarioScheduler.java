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

    public InventarioScheduler(ArticuloRepository articuloRepository,
                               OrdenCompraService ordenCompraService) {
        this.articuloRepository = articuloRepository;
        this.ordenCompraService = ordenCompraService;
    }

    // Se ejecuta cada 2min
    @Scheduled(cron = "0 */2 * * * *") // cada 2 minutos
    public void verificarStockIntervaloFijo() {
        List<Articulo> articulos = articuloRepository.findByModeloInventario(ModeloInventario.INTERVALOFIJO);

        for (Articulo articulo : articulos) {
            if (articulo.getProveedorPredeterminado() == null) continue;

            Integer intervaloDias = articulo.getProveedorPredeterminado().getIntervaloReposicion();
            LocalDateTime ultimaFecha = articulo.getFechaUltimoPedido();
            LocalDateTime ahora = LocalDateTime.now();

            // Si nunca se hizo pedido, lo forzamos
            if (ultimaFecha == null || ultimaFecha.plusDays(intervaloDias).isBefore(ahora)) {
                Optional<OrdenCompra> oc = ordenCompraService.generarOrdenCompraSiCorresponde(articulo.getCodArticulo());
                if (oc.isPresent()) {
                    articulo.setFechaUltimoPedido(ahora);
                    articuloRepository.save(articulo);
                }
            }
        }
    }
}