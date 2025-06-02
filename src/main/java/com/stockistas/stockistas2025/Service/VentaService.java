package com.stockistas.stockistas2025.Service;

import com.stockistas.stockistas2025.Dto.VentaDTO;
import com.stockistas.stockistas2025.Entity.Articulo;
import com.stockistas.stockistas2025.Entity.Venta;
import com.stockistas.stockistas2025.Repository.ArticuloRepository;
import com.stockistas.stockistas2025.Repository.VentaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final ArticuloRepository articuloRepository;
    private final VentaRepository ventaRepository;
    private final OrdenCompraService ordenCompraService;

    /**
     * Registra una venta y reduce el stock del artículo.
     * Verifica que no se venda más stock del disponible.
     * Luego llama a la lógica de generación automática de OC si aplica.
     */
    public void registrarVenta(VentaDTO dto) {
        Articulo articulo = articuloRepository.findById(2)
                .orElseThrow(() -> new EntityNotFoundException("Artículo con ID 2 no encontrado"));

        int stockActual = articulo.getStockActual();
        if (dto.getCantidadVendida() > stockActual) {
            throw new IllegalArgumentException("Stock insuficiente para la venta");
        }

        // Registrar venta
        Venta venta = Venta.builder()
                .articulo(articulo)
                .fechaVenta(LocalDate.now().atStartOfDay())
                .cantProducto(dto.getCantidadVendida())
                .build();

        ventaRepository.save(venta);

        // Actualizar stock
        articulo.setStockActual(stockActual - dto.getCantidadVendida());
        articuloRepository.save(articulo);

        // Verificar si corresponde generar OC automática
        ordenCompraService.generarOrdenCompraSiCorresponde(articulo.getCodArticulo());
    }

    /**
     * Lista todas las ventas realizadas para un artículo dado.
     */
    public List<Venta> listarVentasPorArticulo(Integer codArticulo) {
        Articulo articulo = articuloRepository.findById(codArticulo)
                .orElseThrow(() -> new EntityNotFoundException("Artículo no encontrado"));

        return ventaRepository.findByArticulo(articulo);
    }
}