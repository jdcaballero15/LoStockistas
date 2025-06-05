package com.stockistas.stockistas2025.Service;

import com.stockistas.stockistas2025.Dto.ArticuloProveedorDTO;
import com.stockistas.stockistas2025.Entity.Articulo;
import com.stockistas.stockistas2025.Entity.ArticuloProveedor;
import com.stockistas.stockistas2025.Entity.Proveedor;
import com.stockistas.stockistas2025.Repository.ArticuloProveedorRepository;
import com.stockistas.stockistas2025.Repository.ArticuloRepository;
import com.stockistas.stockistas2025.Repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticuloProveedorService {

    private final ArticuloProveedorRepository articuloProveedorRepo;
    private final ArticuloRepository articuloRepo;
    private final ProveedorRepository proveedorRepo;

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
    public List<ArticuloProveedor> obtenerTodos() {
        return articuloProveedorRepo.findAll();
    }

}