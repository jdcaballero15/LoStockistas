package com.stockistas.stockistas2025.Service;

import com.stockistas.stockistas2025.Dto.ArticuloDTO;
import com.stockistas.stockistas2025.Entity.Articulo;
import com.stockistas.stockistas2025.Entity.ArticuloProveedor;
import com.stockistas.stockistas2025.Entity.ModeloInventario;
import com.stockistas.stockistas2025.Entity.Proveedor;
import com.stockistas.stockistas2025.Repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.apache.commons.math3.distribution.NormalDistribution;

import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticuloService {

    private final ArticuloRepository articuloRepository;
    private final OrdenCompraRepository ordenCompraRepository;
    private final ArticuloProveedorRepository articuloProveedorRepository;

    //-----------------------------------------------------------------------------------------------
    //Creación del artículo
    public Articulo crearArticulo(ArticuloDTO dto) {

        Proveedor proveedor = null;
        // Crear artículo con builder de Lombok
        Articulo articulo = Articulo.builder()
                .nombreArt(dto.getNombreArt())
                .descripArt(dto.getDescripArt())
                .demandaAnual(dto.getDemandaAnual())
                .stockActual(dto.getStockActual())
                .fechaHoraBajaArticulo(null)
                .modeloInventario(dto.getModeloInventario())
                .inventarioMax(dto.getInventarioMax())
                .stockSeguridad(calcularStockSeguridad(dto.getNivelServicio(),dto.getDesviacionEstandar()))
                .nivelServicio(dto.getNivelServicio()/100)
                .desviacionEstandar(dto.getDesviacionEstandar())
                .build();

        return articuloRepository.save(articulo);
    }

    //-----------------------------------------------------------------------------------------------
    //Cálculo del stock de seguridad
    public Integer calcularStockSeguridad(Double nivelServicio, Double desviacionEstandar){
        if (nivelServicio == null || desviacionEstandar == null) {
            throw new IllegalArgumentException("Nivel de servicio y desviación estándar no pueden ser nulos.");
        }

        if (nivelServicio <= 0.0 || nivelServicio >= 1.0) {
            throw new IllegalArgumentException("El nivel de servicio debe estar entre 0 y 1 (sin incluir).");
        }

        if (desviacionEstandar < 0) {
            throw new IllegalArgumentException("La desviación estándar no puede ser negativa.");
        }

        NormalDistribution normal = new NormalDistribution();
        double z = normal.inverseCumulativeProbability(nivelServicio);

        double stockSeguridad = z * desviacionEstandar;

        return (int) Math.ceil(stockSeguridad); // siempre redondeamos hacia arriba
    }

    //-----------------------------------------------------------------------------------------------
    // Cálculo del Punto de Pedido
    public Integer calcularPuntoPedido(Articulo a, Proveedor proveedor) {
        ArticuloProveedor ap = articuloProveedorRepository.findByArticuloAndProveedor(a, proveedor)
                .orElseThrow(() -> new RuntimeException("No se encontró ArticuloProveedor"));

        // (demandaAnual / 365) * demoraEntrega
        BigDecimal demandaDiaria = BigDecimal.valueOf(a.getDemandaAnual()).divide(BigDecimal.valueOf(365), 6, RoundingMode.HALF_UP);
        BigDecimal demoraEntrega = BigDecimal.valueOf(ap.getDemoraEntrega());

        return demandaDiaria.multiply(demoraEntrega).setScale(0, RoundingMode.HALF_UP).intValue();
    }

    //-----------------------------------------------------------------------------------------------
    // Cálculo del Costo de Compra
    public BigDecimal calcularCostoCompra(Articulo a, Proveedor proveedor) {
        ArticuloProveedor ap = articuloProveedorRepository.findByArticuloAndProveedor(a, proveedor)
                .orElseThrow(() -> new RuntimeException("No se encontró ArticuloProveedor"));

        // demandaAnual * precioUnitario
        return BigDecimal.valueOf(a.getDemandaAnual()).multiply(ap.getPrecioUnitario());
    }

    //-----------------------------------------------------------------------------------------------
    // Cálculo del Lote Óptimo (EOQ)
    public Integer calcularLoteOptimo(Articulo a, Proveedor proveedor) {
        ArticuloProveedor ap = articuloProveedorRepository.findByArticuloAndProveedor(a, proveedor)
                .orElseThrow(() -> new RuntimeException("No se encontró ArticuloProveedor"));

        double demandaAnual = a.getDemandaAnual();
        double costoPedido = ap.getCargosPedido().doubleValue();
        double i = a.getI().doubleValue();
        double precioUnitario = ap.getPrecioUnitario().doubleValue();

        double resultado = Math.sqrt((2 * demandaAnual * costoPedido) / (i * precioUnitario));

        return (int) Math.round(resultado);
    }

    //-----------------------------------------------------------------------------------------------
    // Cálculo del Costo de Pedido
    public BigDecimal calcularCostoPedido(Articulo a, Proveedor proveedor) {
        ArticuloProveedor ap = articuloProveedorRepository.findByArticuloAndProveedor(a, proveedor)
                .orElseThrow(() -> new RuntimeException("No se encontró ArticuloProveedor"));

        if (a.getLoteOptimo() == null || a.getLoteOptimo() == 0) {
            throw new IllegalArgumentException("Lote óptimo no puede ser cero o nulo");
        }

        BigDecimal demandaAnual = BigDecimal.valueOf(a.getDemandaAnual());
        BigDecimal loteOptimo = BigDecimal.valueOf(a.getLoteOptimo());
        BigDecimal cargosPedido = ap.getCargosPedido();

        // (demandaAnual / loteOptimo) * cargosPedido
        return demandaAnual.divide(loteOptimo, 6, RoundingMode.HALF_UP).multiply(cargosPedido);
    }

    //-----------------------------------------------------------------------------------------------
    // Cálculo del Costo de Almacenamiento
    public BigDecimal calcularCostoAlmacenamiento(Articulo a, Proveedor proveedor) {
        ArticuloProveedor ap = articuloProveedorRepository.findByArticuloAndProveedor(a, proveedor)
                .orElseThrow(() -> new RuntimeException("No se encontró ArticuloProveedor"));

        if (a.getLoteOptimo() == null) {
            throw new IllegalArgumentException("Lote óptimo no puede ser nulo");
        }

        BigDecimal loteOptimo = BigDecimal.valueOf(a.getLoteOptimo());
        BigDecimal i = a.getI();
        BigDecimal precioUnitario = ap.getPrecioUnitario();
        BigDecimal factor = i.multiply(precioUnitario);

        // (loteOptimo / 2) * (i * precioUnitario)
        return loteOptimo.divide(BigDecimal.valueOf(2), 6, RoundingMode.HALF_UP).multiply(factor);
    }

    //-----------------------------------------------------------------------------------------------
    // Cálculo del CGI
    public BigDecimal calcularCGI(Articulo a, Proveedor proveedor) {

        Optional<ArticuloProveedor> ap = articuloProveedorRepository.findByArticuloAndProveedor(a, proveedor);

        //  costoCompra + costoPedido + costoAlmacenamiento
        BigDecimal D = BigDecimal.valueOf(a.getDemandaAnual());
        BigDecimal C = ap.get().getPrecioUnitario();
        BigDecimal Q = BigDecimal.valueOf(a.getLoteOptimo());
        BigDecimal S = ap.get().getCargosPedido();
        BigDecimal H = a.getCostoAlmacenamiento();

        BigDecimal cgi =
                D.multiply(C)
                        .add(D.divide(Q, 10, RoundingMode.HALF_UP).multiply(S))
                        .add(Q.divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP).multiply(H));

        return cgi;
    }

    //-----------------------------------------------------------------------------------------------
    //Lista todos los articulos que aun no se han dado de baja
    public List<ArticuloDTO> getAll() {
        return articuloRepository.findAll().
                stream()
                .filter(a->a.getFechaHoraBajaArticulo()==null)
                .map(this::toDTO)
                .toList();
    }

    //-----------------------------------------------------------------------------------------------
    //Ajuste de la cantidad de artículos
    public void actualizarStock(Articulo articulo, Integer cantidad){

        articulo.setStockActual(articulo.getStockActual()+cantidad);
        articuloRepository.save(articulo);
    }

    //-----------------------------------------------------------------------------------------------
    //Conversión del articulo encontrado a DTO para devolverlo al front
    public ArticuloDTO toDTO(Articulo articulo) {
        if (articulo == null) return null;
        ArticuloProveedor ap = articulo.getRelacionesConProveedores();

        BigDecimal costoCompra = ap != null ? ap.getPrecioUnitario() : BigDecimal.ZERO;
        BigDecimal costoPedido = ap != null ? ap.getCargosPedido() : BigDecimal.ZERO;

        return ArticuloDTO.builder()
                .codArticulo(articulo.getCodArticulo())
                .nombreArt(articulo.getNombreArt())
                .descripArt(articulo.getDescripArt())
                .demandaAnual(articulo.getDemandaAnual())
                .costoAlmacenamiento(articulo.getCostoAlmacenamiento())
                .costoPedido(costoPedido)
                .costoCompra(costoCompra)
                .stockActual(articulo.getStockActual())
                .CGI(articulo.getCGI())
                .loteOptimo(articulo.getLoteOptimo())
                .puntoPedido(articulo.getPuntoPedido())
                .inventarioMax(articulo.getInventarioMax())
                .stockSeguridad(articulo.getStockSeguridad())
                .urlImagen(articulo.getUrlImagen())
                .modeloInventario(articulo.getModeloInventario())
                .proveedorPredeterminado(articulo.getProveedorPredeterminado())
                .desviacionEstandar(articulo.getDesviacionEstandar())
                .nivelServicio(articulo.getNivelServicio())
                .build();
    }

    //-----------------------------------------------------------------------------------------------
    //Buscamos articulos por codArticulo
    public Articulo getById(Integer id) {
        return articuloRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Artículo no encontrado"));
    }

    //-----------------------------------------------------------------------------------------------
    //Nos llega la informacion modificada del articulo en un DTO y seteamos la nueva información
    public Articulo update(Integer id, ArticuloDTO dto) {
        Articulo existente = getById(id);
        existente.setNombreArt(dto.getNombreArt());
        existente.setDescripArt(dto.getDescripArt());
        existente.setDemandaAnual(dto.getDemandaAnual());
        existente.setStockActual(dto.getStockActual());
        existente.setModeloInventario(dto.getModeloInventario());
        existente.setProveedorPredeterminado(dto.getProveedorPredeterminado());
        existente.setNivelServicio(dto.getNivelServicio()/100);
        existente.setDesviacionEstandar(dto.getDesviacionEstandar());

        //Si al modificar asignamos un proveedor se hacen los cálculos correspondientes
        if (dto.getProveedorPredeterminado() != null) {
            //Si el proveedor tiene intervalo de reposición y nosotros lo utilizabamos con otro modelo de inventario lo modificamos
            if(dto.getProveedorPredeterminado().getIntervaloReposicion() != null && existente.getModeloInventario() == ModeloInventario.LOTEFIJO){
                existente.setModeloInventario(ModeloInventario.INTERVALOFIJO);
            }
            existente.setProveedorPredeterminado(dto.getProveedorPredeterminado());
            existente.setPuntoPedido(calcularPuntoPedido(existente, dto.getProveedorPredeterminado()));
            existente.setLoteOptimo(calcularLoteOptimo(existente, dto.getProveedorPredeterminado()));
            existente.setCostoAlmacenamiento(calcularCostoAlmacenamiento(existente, dto.getProveedorPredeterminado()));
            existente.setCGI(calcularCGI(existente,dto.getProveedorPredeterminado()));
        }

        return articuloRepository.save(existente);
    }

    //-----------------------------------------------------------------------------------------------
    //Eliminamos un articulo siguiendo ciertas condiciones
    public void delete(Integer codArticulo) {

        Articulo articulo = articuloRepository.findById(codArticulo)
                .orElseThrow(() -> new EntityNotFoundException("Artículo con ID " + codArticulo + " no encontrado"));

        //Verificamos si tiene stock
        if (articulo.getStockActual() != null && articulo.getStockActual() > 0) {
            throw new IllegalStateException("No se puede dar de baja: El artículo tiene unidades en stock.");
        }

        ArticuloProveedor ap = articulo.getRelacionesConProveedores();

        boolean tieneOrdenesRelacionadas = ap != null &&
                ordenCompraRepository.existsByDetalles_ArticuloProveedorAndEstado_CodEstadoOCIn(
                        ap,
                        List.of(1, 2) // Estados PENDIENTE o ENVIADA
                );


        if (tieneOrdenesRelacionadas) {
            throw new IllegalStateException("No se puede dar de baja: el artículo tiene órdenes de compra pendientes o enviadas.");
        }

        //Se le da baja lógica al articulo
        articulo.setFechaHoraBajaArticulo(LocalDateTime.now());
        articuloRepository.save(articulo);
    }

    //-----------------------------------------------------------------------------------------------
    //Obtener los artículos críticos
    public List<ArticuloDTO> obtenerArticulosCriticos() {
        List<Articulo> todos = articuloRepository.findAll();

        return todos.stream()
                .filter(a -> a.getStockSeguridad() != null)
                .filter(a -> a.getFechaHoraBajaArticulo() == null) // Activo
                .filter(a -> a.getStockActual() <= a.getStockSeguridad())
                .map(this::toDTO) // Mapeo los datos en su DTO
                .toList();
    }

    //-----------------------------------------------------------------------------------------------
    //Obtener la lista de articulos que hayan alcanzado el punto de pedido y no tengan ordenes en estado (PENDIENTE O ENVIADA)
    public List<ArticuloDTO> obtenerArticulosPuntoPedido(){
        List<Articulo> todosPuntoPedido = articuloRepository.findArticulosConOrdenesQueNoEstanPendientesNiEnviadas();

        return todosPuntoPedido.stream()
                .filter(a -> a.getStockActual()<=a.getPuntoPedido())
                .map(this::toDTO)// Mapeo los datos en su DTO
                .toList();
    }

    //-----------------------------------------------------------------------------------------------
    //Busco proveedores por artículo (se utiliza para seleccionar el proveedor predeterminado filtrando solo aquellos que tengan este tipo de artículo)
    public List<Proveedor> getProveedoresByArticulo(Integer articuloId) {
        Articulo articulo = articuloRepository.findById(articuloId)
                .orElseThrow(() -> new EntityNotFoundException("Artículo no encontrado"));

        // Obtener todos los ArticuloProveedor relacionados con este artículo
        List<ArticuloProveedor> articuloProveedores = articuloProveedorRepository.findByArticulo(articulo);

        // Extraer los proveedores de las relaciones
        return articuloProveedores.stream()
                .map(ArticuloProveedor::getProveedor)
                .distinct()
                .toList();
    }

    //-----------------------------------------------------------------------------------------------
    //Crea el artículo con una foto
    public Articulo crearArticuloConImagen(ArticuloDTO dto) {
        Articulo articulo = Articulo.builder()
                .nombreArt(dto.getNombreArt())
                .descripArt(dto.getDescripArt())
                .demandaAnual(dto.getDemandaAnual())
                .stockActual(dto.getStockActual())
                .fechaHoraBajaArticulo(null)
                .modeloInventario(dto.getModeloInventario())
                .inventarioMax(dto.getInventarioMax())
                .stockSeguridad(dto.getStockSeguridad())
                .nivelServicio(dto.getNivelServicio())
                .desviacionEstandar(dto.getDesviacionEstandar())
                .urlImagen(dto.getUrlImagen())
                .build();

        return articuloRepository.save(articulo);
    }

    //-----------------------------------------------------------------------------------------------
}