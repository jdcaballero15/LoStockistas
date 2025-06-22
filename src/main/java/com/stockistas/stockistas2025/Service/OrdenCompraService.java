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
    private final ArticuloService articuloService;
    private final OrdenCompraRepository ordenCompraRepository;
    private final EstadoOCRepository estadoOCRepository;
    private final ArticuloProveedorRepository articuloProveedorRepository;

    //-----------------------------------------------------------------------------------------------
    /*
     Genera automáticamente una Orden de Compra si el artículo requiere reposición
     y no hay otra orden en curso (PENDIENTE o ENVIADA) para ese artículo.
     */
    public Optional<OrdenCompra> generarOrdenCompraSiCorresponde(Integer codArticulo) {

        Articulo articulo = articuloRepository.findById(codArticulo)
                .orElseThrow(() -> new EntityNotFoundException("Artículo no encontrado"));

        // Obtener relación Artículo-Proveedor predeterminado (clase intermedia)
        ArticuloProveedor relacion = articuloProveedorRepository
                .findByArticuloAndProveedor(articulo, articulo.getProveedorPredeterminado())
                .orElseThrow(() -> new RuntimeException("No existe relación válida con proveedor predeterminado"));

        // Verificar si ya existe una OC con ese Articulo en estado PENDIENTE (1) o ENVIADA (2)
        List<OrdenCompra> ordenesRelacionadas = ordenCompraRepository
                .findByDetalles_ArticuloProveedor_Articulo_CodArticuloAndEstado_CodEstadoOCIn(
                        codArticulo, List.of(1, 2));

        if (!ordenesRelacionadas.isEmpty()) {
            throw new IllegalStateException("Ya existe una orden en proceso para este artículo");
        }

        // Llamo a una función que calcula la cantidad faltante segun el modelo utilizado
        int cantidadFaltante = calcularFaltanteSegunModelo(articulo);
        if (cantidadFaltante <= 0) return Optional.empty();

        // Obtener estado PENDIENTE (1)
        EstadoOC estadoPendiente = estadoOCRepository.findById(1)
                .orElseThrow(() -> new EntityNotFoundException("Estado PENDIENTE no encontrado"));

        // Crear OrdenCompra
        OrdenCompra oc = OrdenCompra.builder()
                .proveedor(articulo.getProveedorPredeterminado())
                .estado(estadoPendiente)
                .cantArt(cantidadFaltante)
                .build();

        // Crear un detalle por cada relación ArticuloProveedor para el artículo (En principio seria solo 1, pero si se compran más artículos a ese proveedor el sistema esta preparado)
        List<DetalleOrdenCompra> detalles = new ArrayList<>();
        Optional<ArticuloProveedor> ap = articuloProveedorRepository.findByArticuloAndProveedor(articulo,articulo.getProveedorPredeterminado());
            BigDecimal subtotal = ap.get().getPrecioUnitario()
                    .add(ap.get().getCargosPedido())
                    .multiply(BigDecimal.valueOf(cantidadFaltante));

            detalles.add(DetalleOrdenCompra.builder()
                    .articuloProveedor(ap.get())
                    .subTotal(subtotal)
                    .build());

            //Seteo los detalles a la Orden de compra
            oc.setDetalles(detalles);

            // Calcular monto total
            BigDecimal montoTotal = detalles.stream()
                .map(DetalleOrdenCompra::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            oc.setMontoCompra(montoTotal);

            // Guardar la orden de compra con toda su información
            OrdenCompra guardada = ordenCompraRepository.save(oc);

            //Asígno la fecha de ultimo pedido (Solo se utiliza en INTERVALO FIJO)
            articulo.setFechaUltimoPedido(LocalDateTime.now());
            articuloRepository.save(articulo);

        return Optional.of(guardada);
    }

    //-----------------------------------------------------------------------------------------------
    //Busco todas las ordenes de compra
    public List<OrdenCompra> getAll() {
        return ordenCompraRepository.findAll();
    }

    //-----------------------------------------------------------------------------------------------
    //Busco la orden de compra por ID
    public OrdenCompra getById(Integer id) {
        return ordenCompraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Orden de compra con ID " + id + " no encontrada"));
    }

    //-----------------------------------------------------------------------------------------------
    //Filtro todas las ordenes de compra de un artículo
    public List<OrdenCompra> getOrdenesCompraByArticulo(Integer codArticulo) {
        return ordenCompraRepository.findByDetalles_ArticuloProveedor_Articulo_CodArticulo(codArticulo);
    }

    //-----------------------------------------------------------------------------------------------
    //Se calcula el stock faltante segun el modelo utilizado
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
        return 0; // Por seguridad
    }

    //-----------------------------------------------------------------------------------------------
    //Creo una orden de compra manualmente
    public OrdenCompra crearOrdenCompraManual(OrdenCompraDTO dto) {

        Articulo articulo = articuloRepository.findById(dto.getCodArticulo())
                .orElseThrow(() -> new EntityNotFoundException("Artículo no encontrado"));

        Proveedor proveedor = articulo.getProveedorPredeterminado();

        ArticuloProveedor relacion = articuloProveedorRepository
                .findByArticuloAndProveedor(articulo, proveedor)
                .orElseThrow(() -> new RuntimeException("No existe relación válida entre artículo y proveedor"));

        EstadoOC estadoPendiente = estadoOCRepository.findById(1)
                .orElseThrow(() -> new EntityNotFoundException("Estado PENDIENTE no encontrado"));

        //Verifico que la cantidad a comprar no sobrepase mi límite máximo
        Integer cantidad;
        if (dto.getCantidad() != null) {
            if (dto.getCantidad() > articulo.getInventarioMax()) {
                throw new IllegalArgumentException("La cantidad solicitada excede el inventario máximo permitido para este artículo.");
            }
            cantidad = dto.getCantidad();
        } else {
            cantidad = articulo.getLoteOptimo(); // Valor por defecto
        }

        BigDecimal subtotalUnitario = relacion.getPrecioUnitario().add(relacion.getCargosPedido());

        OrdenCompra oc = OrdenCompra.builder()
                .estado(estadoPendiente)
                .cantArt(cantidad)
                .proveedor(articulo.getProveedorPredeterminado())
                .build();

        // Crear un detalle por cada relación ArticuloProveedor para el artículo
        List<DetalleOrdenCompra> detalles = new ArrayList<>();
        Optional<ArticuloProveedor> ap = articuloProveedorRepository.findByArticuloAndProveedor(articulo,articulo.getProveedorPredeterminado());
            BigDecimal subtotal = ap.get().getPrecioUnitario()
                    .add(ap.get().getCargosPedido())
                    .multiply(BigDecimal.valueOf(cantidad));

            detalles.add(DetalleOrdenCompra.builder()
                    .articuloProveedor(ap.get())
                    .subTotal(subtotal)
                    .build());

        // Calcular monto total
        BigDecimal montoTotal = detalles.stream()
                .map(DetalleOrdenCompra::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        oc.setDetalles(detalles);
        oc.setMontoCompra(montoTotal);

        //Asígno la fecha de ultimo pedido (Solo se utiliza en INTERVALO FIJO)
        articulo.setFechaUltimoPedido(LocalDateTime.now());

        articuloRepository.save(articulo);

        return ordenCompraRepository.save(oc);
    }

    //-----------------------------------------------------------------------------------------------
    //Cancelar orden de compra
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
    //Cambio de estado de la orden de compra a ENVIADA (2)
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
    //Cambio de estado de la orden de compra a FINALIZADA (3)
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

            Articulo articulo = detalle.getArticuloProveedor().getArticulo();

            // Actualizar el stock del artículo
            articuloService.actualizarStock(articulo,oc.getCantArt());
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

    //-----------------------------------------------------------------------------------------------

}

