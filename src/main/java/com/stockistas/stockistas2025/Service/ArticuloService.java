package com.stockistas.stockistas2025.Service;

import com.stockistas.stockistas2025.Dto.ArticuloDTO;
import com.stockistas.stockistas2025.Entity.Articulo;
import com.stockistas.stockistas2025.Entity.Proveedor;
import com.stockistas.stockistas2025.Repository.ArticuloRepository;
import com.stockistas.stockistas2025.Repository.ProveedorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class ArticuloService {

    private final ArticuloRepository articuloRepository;
    private final ProveedorRepository proveedorRepository;

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
}