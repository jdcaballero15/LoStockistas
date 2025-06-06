package com.stockistas.stockistas2025.Service;

import com.stockistas.stockistas2025.Dto.ArticuloDTO;
import com.stockistas.stockistas2025.Entity.Articulo;
import com.stockistas.stockistas2025.Entity.Proveedor;
import com.stockistas.stockistas2025.Repository.ArticuloRepository;
import com.stockistas.stockistas2025.Repository.OrdenCompraRepository;
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
    private final OrdenCompraRepository ordenCompraRepository;

    public Articulo crearArticulo(ArticuloDTO dto) {

        Proveedor proveedor = null;

        if (dto.getProveedorPredeterminado() != null && dto.getProveedorPredeterminado().getCodProveedor() != null) {
            proveedor = proveedorRepository.findById(dto.getProveedorPredeterminado().getCodProveedor())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Proveedor con ID " + dto.getProveedorPredeterminado().getCodProveedor() + " no encontrado"));
        }
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
                .loteOptimo(dto.getLoteOptimo())
                .inventarioMax(dto.getInventarioMax())
                .puntoPedido(dto.getPuntoPedido())
                .stockSeguridad(dto.getStockSeguridad())
                .build();

        return articuloRepository.save(articulo);
    }

    //Metodo para calcular el CGI
    private BigDecimal calcularCGI(ArticuloDTO dto) {
        // Fórmula: CGI = (Demanda * CostoCompra) + CostoPedido + CostoAlmacenamiento
        BigDecimal demanda = BigDecimal.valueOf(dto.getDemandaAnual());
        return demanda.multiply(dto.getCostoCompra())
                .add(dto.getCostoPedido())
                .add(dto.getCostoAlmacenamiento());
    }

    //Lista todos los articulos que aun no se han dado de baja y los devuelve como un DTO con la informacion necesaria
    public List<ArticuloDTO> getAll() {
        return articuloRepository.findAll().stream().filter(a->a.getFechaHoraBajaArticulo()==null).map(this::toDTO).toList();
    }

    //Conversión del articulo encontrado a DTO para devolverlo al front
    public ArticuloDTO toDTO(Articulo articulo) {
        if (articulo == null) return null;

        return ArticuloDTO.builder()
                .codArticulo(articulo.getCodArticulo())
                .nombreArt(articulo.getNombreArt())
                .descripArt(articulo.getDescripArt())
                .demandaAnual(articulo.getDemandaAnual())
                .costoAlmacenamiento(articulo.getCostoAlmacenamiento())
                .costoPedido(articulo.getCostoPedido())
                .costoCompra(articulo.getCostoCompra())
                .stockActual(articulo.getStockActual())
                .CGI(articulo.getCGI())
                .loteOptimo(articulo.getLoteOptimo())
                .puntoPedido(articulo.getPuntoPedido())
                .inventarioMax(articulo.getInventarioMax())
                .stockSeguridad(articulo.getStockSeguridad())
                .modeloInventario(articulo.getModeloInventario())
                .proveedorPredeterminado(articulo.getProveedorPredeterminado())
                .build();
    }

    //Buscamos articulos por codArticulo
    public Articulo getById(Integer id) {
        return articuloRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Artículo no encontrado"));
    }

    //Nos llega la informacion modificada del articulo en un DTO y seteamos la nueva información
    public Articulo update(Integer id, ArticuloDTO dto) {
        Articulo existente = getById(id);
        existente.setNombreArt(dto.getNombreArt());
        existente.setDescripArt(dto.getDescripArt());
        existente.setDemandaAnual(dto.getDemandaAnual());
        existente.setCostoAlmacenamiento(dto.getCostoAlmacenamiento());
        existente.setCostoPedido(dto.getCostoPedido());
        existente.setCostoCompra(dto.getCostoCompra());
        existente.setStockActual(dto.getStockActual());
        existente.setProveedorPredeterminado(dto.getProveedorPredeterminado());
        existente.setModeloInventario(dto.getModeloInventario());

        // Recalculamos el CGI
        existente.setCGI(calcularCGI(dto));

        return articuloRepository.save(existente);
    }

    //Eliminamos un articulo siguiendo ciertas condiciones
    public void delete(Integer codArticulo) {

        Articulo articulo = articuloRepository.findById(codArticulo)
                .orElseThrow(() -> new EntityNotFoundException("Artículo con ID " + codArticulo + " no encontrado"));

        //Verificamos si tiene stock
        if (articulo.getStockActual() != null && articulo.getStockActual() > 0) {
            throw new IllegalStateException("No se puede dar de baja: el artículo tiene unidades en stock.");
        }

        //Verificamos si tiene órdenes de compra en estado pendiente o enviada
        boolean tieneOrdenesRelacionadas = articulo.getRelacionesConProveedores().stream()
                .anyMatch(ap -> ordenCompraRepository.existsByDetalles_ArticuloProveedorAndEstado_CodEstadoOCIn(
                        ap,
                        List.of(1, 2) // Estados PENDIENTE o ENVIADA
                ));

        if (tieneOrdenesRelacionadas) {
            throw new IllegalStateException("No se puede dar de baja: el artículo tiene órdenes de compra pendientes o enviadas.");
        }

        //Se le da baja logica al articulo
        articulo.setFechaHoraBajaArticulo(LocalDateTime.now());
        articuloRepository.save(articulo);
    }

    public List<Articulo> obtenerArticulosCriticos() {
        List<Articulo> todos = articuloRepository.findAll();
        return todos.stream()
                .filter(a -> a.getStockSeguridad() !=null)
                .filter(a -> a.getFechaHoraBajaArticulo() == null) // activo
                .filter(a -> a.getStockActual() <= a.getStockSeguridad())
                .toList();
    }
}