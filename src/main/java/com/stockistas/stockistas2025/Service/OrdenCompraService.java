package com.stockistas.stockistas2025.Service;
import com.stockistas.stockistas2025.Dto.OrdenCompraDTO;
import com.stockistas.stockistas2025.Entity.*;
import com.stockistas.stockistas2025.Repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrdenCompraService {

    private final ArticuloRepository articuloRepository;
    private final ProveedorRepository proveedorRepository;
    private final OrdenCompraRepository ordenCompraRepository;
    private final EstadoOCRepository estadoOCRepository;
    private final ArticuloProveedorRepository articuloProveedorRepository;
    private final DetalleOrdenCompraRepository detalleOrdenCompraRepository;

    //-----------------------------------------------------------------------------------------------
    /*
     Genera automáticamente una Orden de Compra si el artículo requiere reposición
     y no hay otra orden en curso (Pendiente o Enviada) para ese artículo.
     */
    public Optional<OrdenCompra> generarOrdenCompraSiCorresponde(Integer codArticulo) {
        Articulo articulo = articuloRepository.findById(codArticulo)
                .orElseThrow(() -> new EntityNotFoundException("Artículo no encontrado"));

        System.out.println("ENCONTRO A:"+articulo.getNombreArt());
        System.out.println("SU PROVEEDOR PRED ES: "+articulo.getProveedorPredeterminado().getCodProveedor());
        // Obtener relación Artículo-Proveedor predeterminado
        ArticuloProveedor relacion = articuloProveedorRepository
                .findByArticuloAndProveedor(articulo, articulo.getProveedorPredeterminado())
                .orElseThrow(() -> new RuntimeException("No existe relación válida con proveedor predeterminado"));
        System.out.println("SE ENCONTRÓ ARTICULOPROVEEDOR");


        // Verificar si ya existe una OC con ese Articulo en estado Pendiente (1) o Enviada (2)
        List<OrdenCompra> ordenesRelacionadas = ordenCompraRepository
                .findByDetalles_ArticuloProveedor_Articulo_CodArticuloAndEstado_CodEstadoOCIn(
                        codArticulo, List.of(1, 2));

        if (!ordenesRelacionadas.isEmpty()) {
            System.out.println("YA EXISTE ORDEN EN PROCESO PARA ESTE ARTÍCULO");
            return Optional.empty();
        }
        System.out.println("NO EXISTE ORDEN EN PROCESO PARA ESTE ARTÍCULO");

        // Calcular faltante
        int cantidadFaltante = calcularFaltanteSegunModelo(articulo);
        if (cantidadFaltante <= 0) return Optional.empty();

        // Obtener estado PENDIENTE (código 1)
        EstadoOC estadoPendiente = estadoOCRepository.findById(1)
                .orElseThrow(() -> new EntityNotFoundException("Estado PENDIENTE no encontrado"));

        // Crear OrdenCompra
        OrdenCompra oc = OrdenCompra.builder()
                .proveedor(articulo.getProveedorPredeterminado())
                .estado(estadoPendiente)
                .cantArt(cantidadFaltante)
                .build();

        // Crear un detalle por cada relación ArticuloProveedor para el artículo
        List<DetalleOrdenCompra> detalles = new ArrayList<>();
        for (ArticuloProveedor ap : articulo.getRelacionesConProveedores()) {
            BigDecimal subtotal = ap.getPrecioUnitario()
                    .add(ap.getCargosPedido())
                    .multiply(BigDecimal.valueOf(cantidadFaltante));

            detalles.add(DetalleOrdenCompra.builder()
                    .articuloProveedor(ap)
                    .subTotal(subtotal)
                    .build());
        }
        oc.setDetalles(detalles);
        // Calcular monto total
        BigDecimal montoTotal = detalles.stream()
                .map(DetalleOrdenCompra::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        oc.setMontoCompra(montoTotal);
        // Guardar en cascada
        OrdenCompra guardada = ordenCompraRepository.save(oc);

        articulo.setFechaUltimoPedido(LocalDateTime.now());
        articuloRepository.save(articulo);

        return Optional.of(guardada);
    }

    //-----------------------------------------------------------------------------------------------
    public List<OrdenCompra> getAll() {
        return ordenCompraRepository.findAll();
    }

    //-----------------------------------------------------------------------------------------------
    public OrdenCompra getById(Integer id) {
        return ordenCompraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orden de compra con ID " + id + " no encontrada"));
    }

    //-----------------------------------------------------------------------------------------------
    public List<OrdenCompra> getOrdenesCompraByArticulo(Integer codArticulo) {
        return ordenCompraRepository.findByDetalles_ArticuloProveedor_Articulo_CodArticulo(codArticulo);
    }

    //-----------------------------------------------------------------------------------------------
    public int calcularFaltanteSegunModelo(Articulo articulo) {
        int stockActual = articulo.getStockActual();
        ModeloInventario modelo = articulo.getModeloInventario();

        if (modelo == ModeloInventario.LOTEFIJO) {
            int puntoDePedido = articulo.getPuntoPedido();
            if (stockActual <= puntoDePedido+articulo.getStockSeguridad()) {
                int loteOptimo = articulo.getLoteOptimo();
                return loteOptimo;
            } else {
                return 0;
            }
        } else if (modelo == ModeloInventario.INTERVALOFIJO) {
            int stockSeguridad = articulo.getStockSeguridad();
            if (stockActual <= stockSeguridad) {
                int inventarioMaximo = articulo.getInventarioMax();

                return inventarioMaximo - stockActual;
            } else {
                return 0;
            }
        }
        return 0; // por seguridad
    }

    //-----------------------------------------------------------------------------------------------
    public OrdenCompra crearOrdenCompraManual(OrdenCompraDTO dto) {
        Articulo articulo = articuloRepository.findById(dto.getCodArticulo())
                .orElseThrow(() -> new EntityNotFoundException("Artículo no encontrado"));

        Proveedor proveedor = articulo.getProveedorPredeterminado();

        ArticuloProveedor relacion = articuloProveedorRepository
                .findByArticuloAndProveedor(articulo, proveedor)
                .orElseThrow(() -> new RuntimeException("No existe relación válida entre artículo y proveedor"));

        EstadoOC estadoPendiente = estadoOCRepository.findById(1)
                .orElseThrow(() -> new EntityNotFoundException("Estado PENDIENTE no encontrado"));

        Integer cantidad;
        if (dto.getCantidad() != null) {
            if (dto.getCantidad() > articulo.getInventarioMax()) {
                throw new IllegalArgumentException("La cantidad solicitada excede el inventario máximo permitido para este artículo.");
            }
            cantidad = dto.getCantidad();
        } else {
            cantidad = articulo.getLoteOptimo(); // valor por defecto
        }

        BigDecimal subtotalUnitario = relacion.getPrecioUnitario().add(relacion.getCargosPedido());

        OrdenCompra oc = OrdenCompra.builder()
                .estado(estadoPendiente)
                .cantArt(cantidad)

                .proveedor(articulo.getProveedorPredeterminado())
                .build();

        // Crear un detalle por cada relación ArticuloProveedor para el artículo
        List<DetalleOrdenCompra> detalles = new ArrayList<>();
        for (ArticuloProveedor ap : articulo.getRelacionesConProveedores()) {
            BigDecimal subtotal = ap.getPrecioUnitario()
                    .add(ap.getCargosPedido())
                    .multiply(BigDecimal.valueOf(cantidad));

            detalles.add(DetalleOrdenCompra.builder()
                    .articuloProveedor(ap)
                    .subTotal(subtotal)
                    .build());
        }

        // Calcular monto total
        BigDecimal montoTotal = detalles.stream()
                .map(DetalleOrdenCompra::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        oc.setDetalles(detalles);
        oc.setMontoCompra(montoTotal);

        articulo.setFechaUltimoPedido(LocalDateTime.now());
        articuloRepository.save(articulo);

        return ordenCompraRepository.save(oc);
    }

    //-----------------------------------------------------------------------------------------------
    public void cancelarOrdenCompra(Integer id) {
        OrdenCompra oc = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orden de Compra no encontrada"));

        if (oc.getEstado().getCodEstadoOC() != 1)
            throw new IllegalStateException("Solo se pueden cancelar órdenes en estado PENDIENTE");

        EstadoOC estadoCancelada = estadoOCRepository.findById(3)
                .orElseThrow(() -> new EntityNotFoundException("Estado CANCELADA no encontrado"));

        oc.setEstado(estadoCancelada);
        ordenCompraRepository.save(oc);
    }

    //-----------------------------------------------------------------------------------------------
    public void enviarOrdenCompra(Integer id) {
        OrdenCompra oc = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orden de Compra no encontrada"));

    if (oc.getEstado().getCodEstadoOC() != 1)
            throw new IllegalStateException("Solo se pueden enviar órdenes en estado PENDIENTE");


        EstadoOC estadoEnviada = estadoOCRepository.findById(2)
                .orElseThrow(() -> new EntityNotFoundException("Estado ENVIADA no encontrado"));

        oc.setEstado(estadoEnviada);
        ordenCompraRepository.save(oc);
    }

    //-----------------------------------------------------------------------------------------------
    public void finalizarOrdenCompra(Integer id) {
        OrdenCompra oc = ordenCompraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orden de Compra no encontrada"));
        if (oc.getEstado().getCodEstadoOC() != 2)
            throw new IllegalStateException("Solo se pueden enviar órdenes en estado ENVIADO");
        EstadoOC estadoFinalizada = estadoOCRepository.findById(4)
                .orElseThrow(() -> new EntityNotFoundException("Estado FINALIZADA no encontrado"));

        oc.setEstado(estadoFinalizada);
        ordenCompraRepository.save(oc);
        for (DetalleOrdenCompra detalle : oc.getDetalles()) {
            detalle.getArticuloProveedor().getArticulo().setStockActual(detalle.getArticuloProveedor().getArticulo().getStockActual() + oc.getCantArt());
            articuloRepository.save(detalle.getArticuloProveedor().getArticulo());
        }
    }

    //-----------------------------------------------------------------------------------------------
    public OrdenCompra editarCantidadOrdenCompra(Integer idOC, Integer nuevaCantidad) {
        OrdenCompra oc = ordenCompraRepository.findById(idOC)
                .orElseThrow(() -> new EntityNotFoundException("Orden de compra no encontrada"));

        // Validar estado
        if (oc.getEstado().getCodEstadoOC() != 1) {
            throw new IllegalStateException("Solo se puede editar una orden en estado PENDIENTE");
        }

        // Actualizar cantidad total
        oc.setCantArt(nuevaCantidad);

        // Recalcular subtotales en cada detalle
        List<DetalleOrdenCompra> detalles = oc.getDetalles();
        BigDecimal nuevoMontoTotal = BigDecimal.ZERO;

        for (DetalleOrdenCompra detalle : detalles) {
            ArticuloProveedor ap = detalle.getArticuloProveedor();
            BigDecimal nuevoSubtotal = ap.getPrecioUnitario()
                    .add(ap.getCargosPedido())
                    .multiply(BigDecimal.valueOf(nuevaCantidad));

            detalle.setSubTotal(nuevoSubtotal);
            nuevoMontoTotal = nuevoMontoTotal.add(nuevoSubtotal);
        }

        oc.setMontoCompra(nuevoMontoTotal);

        return ordenCompraRepository.save(oc);
    }

}

