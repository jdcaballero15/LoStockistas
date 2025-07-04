package com.stockistas.stockistas2025.Service;

import com.stockistas.stockistas2025.Dto.ArticuloProveedorDTO;
import com.stockistas.stockistas2025.Dto.ArticuloProveedorResponseDTO;
import com.stockistas.stockistas2025.Dto.ProveedorDTO;
import com.stockistas.stockistas2025.Dto.ProveedorDTOOutput;
import com.stockistas.stockistas2025.Entity.Articulo;
import com.stockistas.stockistas2025.Entity.ArticuloProveedor;
import com.stockistas.stockistas2025.Entity.Proveedor;
import com.stockistas.stockistas2025.Repository.ArticuloProveedorRepository;
import com.stockistas.stockistas2025.Repository.ArticuloRepository;
import com.stockistas.stockistas2025.Repository.OrdenCompraRepository;
import com.stockistas.stockistas2025.Repository.ProveedorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ArticuloRepository articuloRepository;
    private final ArticuloProveedorRepository articuloProveedorRepository;
    private final OrdenCompraRepository ordenCompraRepository;

    //-----------------------------------------------------------------------------------------------
    // Alta de proveedor junto con sus artículos asociados (Tiene un artículo como mínimo)
    public Proveedor altaProveedor(ProveedorDTO dto) {

        if (dto.getArticulos() == null || dto.getArticulos().isEmpty()) {
            throw new IllegalArgumentException("Debe asociarse al menos un artículo al proveedor.");
        }

        Proveedor proveedor = Proveedor.builder()
                .nombreProveedor(dto.getNombre())
                .emailProveedor(dto.getCorreo())
                .telefonoProveedor(dto.getTelefono())
                .direccionProveedor(dto.getDireccion())
                .intervaloReposicion(dto.getIntervaloReposicion())
                .build();

        Proveedor proveedorGuardado = proveedorRepository.save(proveedor);

        for (ArticuloProveedorDTO ap : dto.getArticulos()) {
            Articulo articulo = articuloRepository.findById(ap.getCodArticulo())
                    .orElseThrow(() -> new EntityNotFoundException("Artículo no encontrado: " + ap.getCodArticulo()));

            ArticuloProveedor relacion = ArticuloProveedor.builder()
                    .proveedor(proveedorGuardado)
                    .articulo(articulo)
                    .precioUnitario(ap.getPrecioUnitario())
                    .cargosPedido(ap.getCargosPedido())
                    .demoraEntrega(ap.getDemoraEntregaDias())
                    .build();

            articuloProveedorRepository.save(relacion);
        }

        return proveedorGuardado;
    }

    //-----------------------------------------------------------------------------------------------
    // Baja lógica del proveedor.
    public void bajaProveedor(Integer codProveedor) {

        Proveedor proveedor = proveedorRepository.findById(codProveedor)
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado."));

        boolean esPredeterminado = articuloRepository.existsByProveedorPredeterminado(proveedor);
        if (esPredeterminado) {
            throw new IllegalStateException("No se puede eliminar: proveedor es predeterminado en al menos un artículo.");
        }

        boolean tieneOCActiva = ordenCompraRepository.existsByProveedorAndEstado_CodEstadoOCIn(
                proveedor, List.of(1, 2));
        if (tieneOCActiva) {
            throw new IllegalStateException("No se puede eliminar: proveedor tiene órdenes de compra pendientes o enviadas.");
        }

        proveedor.setFechaHoraBajaProveedor(LocalDateTime.now());
        proveedorRepository.save(proveedor);
    }

    //-----------------------------------------------------------------------------------------------
    //Listo los artículos del proveedor y los guardo en un DTO para enviarlos al front
    public List<ArticuloProveedorResponseDTO> listarArticulosPorProveedor(Integer codProveedor) {

        Proveedor proveedor = proveedorRepository.findById(codProveedor)
                .orElseThrow(() -> new EntityNotFoundException("Proveedor no encontrado."));

        List<ArticuloProveedor> relaciones = articuloProveedorRepository.findByProveedor(proveedor);

        return relaciones.stream()
                .map(relacion -> {
                    Articulo articulo = relacion.getArticulo();
                    boolean esPredeterminado = proveedor.equals(articulo.getProveedorPredeterminado());
                    return new ArticuloProveedorResponseDTO(
                            articulo.getCodArticulo(),
                            articulo.getNombreArt(),
                            esPredeterminado
                    );
                })
                .collect(Collectors.toList());
    }

    //-----------------------------------------------------------------------------------------------
    //Muestro todos los proveedores y los guardo en un DTO para enviarlos al front
    public List<ProveedorDTOOutput> getAll() {
        return proveedorRepository.findAll().stream()
                .filter(p -> p.getFechaHoraBajaProveedor() == null)
                .map(p -> new ProveedorDTOOutput(
                        p.getCodProveedor(),
                        p.getNombreProveedor(),
                        p.getDireccionProveedor(),
                        p.getTelefonoProveedor(),
                        p.getEmailProveedor()))
                .collect(Collectors.toList());
    }

    //-----------------------------------------------------------------------------------------------
}
