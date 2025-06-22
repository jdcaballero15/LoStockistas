package com.stockistas.stockistas2025.Service;

import com.stockistas.stockistas2025.Dto.ArticuloProveedorDTO;
import com.stockistas.stockistas2025.Dto.ProveedorDTOOutput;
import com.stockistas.stockistas2025.Entity.Articulo;
import com.stockistas.stockistas2025.Entity.ArticuloProveedor;
import com.stockistas.stockistas2025.Entity.Proveedor;
import com.stockistas.stockistas2025.Repository.ArticuloProveedorRepository;
import com.stockistas.stockistas2025.Repository.ArticuloRepository;
import com.stockistas.stockistas2025.Repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticuloProveedorService {

    private final ArticuloProveedorRepository articuloProveedorRepo;
    private final ArticuloRepository articuloRepo;
    private final ProveedorRepository proveedorRepo;

    //-----------------------------------------------------------------------------------------------
    //Genero la relación entre los aticulos que vende el proveedor y los de nuestro sistema (clase intermedia)
    public ArticuloProveedor agregarRelacion(ArticuloProveedorDTO dto, Integer codProveedor) {

        Articulo articulo = articuloRepo.findById(dto.getCodArticulo())
                .orElseThrow(() -> new IllegalArgumentException("Artículo no encontrado"));

        Proveedor proveedor = proveedorRepo.findById(codProveedor)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));

        ArticuloProveedor ap = ArticuloProveedor.builder()
                .articulo(articulo)
                .proveedor(proveedor)
                .precioUnitario(dto.getPrecioUnitario())
                .cargosPedido(dto.getCargosPedido())
                .demoraEntrega(dto.getDemoraEntregaDias())
                .build();

        return articuloProveedorRepo.save(ap);
    }

    //-----------------------------------------------------------------------------------------------
    //Lista de todos los artículos (clase intermedia)
    public List<ArticuloProveedor> obtenerTodos() {
        return articuloProveedorRepo.findAll();
    }

    //-----------------------------------------------------------------------------------------------
    //El proveedor ya no tiene disponible este artículo
    public void eliminarRelacion(Integer codProveedor, Integer codArticulo) {
        Articulo articulo = articuloRepo.findById(codArticulo)
                .orElseThrow(() -> new IllegalArgumentException("Artículo no encontrado"));

        Proveedor proveedor = proveedorRepo.findById(codProveedor)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));

        ArticuloProveedor relacion = articuloProveedorRepo.findByArticuloAndProveedor(articulo, proveedor)
                .orElseThrow(() -> new IllegalArgumentException("Relación no encontrada"));

        articuloProveedorRepo.delete(relacion);
    }

    //-----------------------------------------------------------------------------------------------

    public List<ArticuloProveedorDTO> obtenerPorArticulo(Articulo articulo) {
        List<ArticuloProveedor> relaciones = articuloProveedorRepo.findByArticulo(articulo);

        return relaciones.stream()
                .map(ap -> {
                    ArticuloProveedorDTO dto = new ArticuloProveedorDTO();
                    dto.setCodArticulo(ap.getArticulo().getCodArticulo());
                    dto.setPrecioUnitario(ap.getPrecioUnitario());
                    dto.setCargosPedido(ap.getCargosPedido());
                    dto.setDemoraEntregaDias(ap.getDemoraEntrega());

                    Proveedor proveedor = ap.getProveedor();
                    ProveedorDTOOutput proveedorDTO = ProveedorDTOOutput.builder()
                            .codProveedor(proveedor.getCodProveedor())
                            .nombreProveedor(proveedor.getNombreProveedor())
                            .emailProveedor(proveedor.getEmailProveedor())
                            .telefonoProveedor(proveedor.getTelefonoProveedor())
                            .direccionProveedor(proveedor.getDireccionProveedor())
                            .build();

                    dto.setProveedorDTOOutput(proveedorDTO);
                    return dto;
                })
                .toList();
    }

}