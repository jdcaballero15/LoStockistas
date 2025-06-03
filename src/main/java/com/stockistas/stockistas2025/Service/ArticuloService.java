package com.stockistas.stockistas2025.Service;

import com.stockistas.stockistas2025.Dto.ArticuloDTO;
import com.stockistas.stockistas2025.Entity.Articulo;
import com.stockistas.stockistas2025.Entity.Proveedor;
import com.stockistas.stockistas2025.Repository.ArticuloRepository;
import com.stockistas.stockistas2025.Repository.ProveedorRepository;
import com.stockistas.stockistas2025.Repository.DetalleOrdenCompraRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ArticuloService {

    private final ArticuloRepository articuloRepository;
    private final ProveedorRepository proveedorRepository;
    private final DetalleOrdenCompraRepository detalleOrdenCompraRepository;

    public Articulo crearArticulo(ArticuloDTO dto) {
        // Buscar proveedor
        Proveedor proveedor = proveedorRepository.findById(dto.getProveedorId())
                .orElseThrow(() -> new EntityNotFoundException("Proveedor con ID " + dto.getProveedorId() + " no encontrado"));

        // Calcular CGI inicial
        BigDecimal CGI = calcularCGI(dto);

        // Crear artículo con builder de Lombok
        Articulo articulo = Articulo.builder()
                .nombreArt(dto.getNombreArt())
                .descripArt(dto.getDescripArt())
                .demandaAnual(dto.getDemandaAnual())
                .costoAlmacenamiento(dto.getCostoAlmacenamiento())
                .costoPedido(dto.getCostoPedido())
                .costoCompra(dto.getCostoCompra())
                .stockActual(dto.getStockActual())
                .fechaHoraBajaArticulo(null)
                .CGI(CGI)
                .modeloInventario(dto.getModeloInventario())
                .proveedorPredeterminado(proveedor)
                .relacionesConProveedores(new ArrayList<>())
                .ventas(new ArrayList<>())
                .build();

        return articuloRepository.save(articulo);
    }

    private BigDecimal calcularCGI(ArticuloDTO dto) {
        // Fórmula: CGI = (Demanda * CostoCompra) + CostoPedido + CostoAlmacenamiento
        BigDecimal demanda = BigDecimal.valueOf(dto.getDemandaAnual());
        return demanda.multiply(dto.getCostoCompra())
                .add(dto.getCostoPedido())
                .add(dto.getCostoAlmacenamiento());
    }

    public List<Articulo> getAll() {
        return articuloRepository.findAll();
    }

    public Articulo getById(Integer id) {
        return articuloRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Artículo no encontrado"));
    }

    public Articulo update(Integer id, Articulo articulo) {
        Articulo existente = getById(id);
        existente.setNombreArt(articulo.getNombreArt());
        existente.setDescripArt(articulo.getDescripArt());
        existente.setDemandaAnual(articulo.getDemandaAnual());
        existente.setCostoAlmacenamiento(articulo.getCostoAlmacenamiento());
        existente.setCostoPedido(articulo.getCostoPedido());
        existente.setCostoCompra(articulo.getCostoCompra());
        existente.setStockActual(articulo.getStockActual());
        existente.setProveedorPredeterminado(articulo.getProveedorPredeterminado());
        existente.setModeloInventario(articulo.getModeloInventario());
        return articuloRepository.save(existente);
    }

    public void delete(Integer codArticulo) {
        Articulo articulo = articuloRepository.findById(codArticulo)
                .orElseThrow(() -> new EntityNotFoundException("Artículo con ID " + codArticulo + " no encontrado"));

        // 1. Verificar si tiene stock
        if (articulo.getStockActual() != null && articulo.getStockActual() > 0) {
            throw new IllegalStateException("No se puede dar de baja: el artículo tiene unidades en stock.");
        }

        // 2. Verificar si tiene órdenes de compra en estado pendiente o enviada
        boolean tieneOrdenesRelacionadas = articulo.getRelacionesConProveedores().stream()
                .anyMatch(ap -> detalleOrdenCompraRepository.existsByArticuloProveedorAndOrdenCompra_Estado_CodEstadoOCIn(
                        ap,
                        List.of(1, 2) // Suponiendo que 1 = PENDIENTE, 2 = ENVIADA
                ));

        if (tieneOrdenesRelacionadas) {
            throw new IllegalStateException("No se puede dar de baja: el artículo tiene órdenes de compra pendientes o enviadas.");
        }

        // 3. Dar de baja (baja lógica)
        articulo.setFechaHoraBajaArticulo(LocalDateTime.now());
        articuloRepository.save(articulo);
    }
}