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
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final ArticuloRepository articuloRepository;
    private final ArticuloService articuloService;
    private final VentaRepository ventaRepository;
    private final OrdenCompraService ordenCompraService;

    //-----------------------------------------------------------------------------------------------
    //Registro la venta y llamo a la función para actualizar stock
    public Venta registrarVenta(VentaDTO dto) {

        Articulo articulo = articuloRepository.findById(dto.getCodArticulo())
                .orElseThrow(() -> new EntityNotFoundException("Artículo con ID " + dto.getCodArticulo() + " no encontrado"));

        // Verificar que se ingreso una cantidad
        int stockActual = articulo.getStockActual();
        if (dto.getCantidadVendida() == null || dto.getCantidadVendida() <= 0) {
            throw new IllegalArgumentException("La cantidad vendida debe ser mayor que cero.");
        }
        //Verificar que no se venda más del stock disponible
        if (dto.getCantidadVendida() > stockActual) {
            throw new IllegalArgumentException("Stock insuficiente para la venta");
        }

        // Registrar la venta
        Venta venta = Venta.builder()
                .articulo(articulo)
                .fechaVenta(LocalDateTime.now())
                .cantProducto(dto.getCantidadVendida())
                .build();

        Venta ventaGuardada = ventaRepository.save(venta);

        // Actualizar el stock del artículo
        articuloService.actualizarStock(articulo,-dto.getCantidadVendida());

        // Verificar si se debe generar una Orden de Compra automática
        ordenCompraService.generarOrdenCompraSiCorresponde(articulo.getCodArticulo());

        return ventaGuardada;
    }

    //-----------------------------------------------------------------------------------------------
    //Lista todas las ventas realizadas para un artículo dado.
    public List<Venta> listarVentasPorArticulo(Integer codArticulo) {

        Articulo articulo = articuloRepository.findById(codArticulo)
                .orElseThrow(() -> new EntityNotFoundException("Artículo no encontrado"));

        return ventaRepository.findByArticulo(articulo);
    }

    //-----------------------------------------------------------------------------------------------
}