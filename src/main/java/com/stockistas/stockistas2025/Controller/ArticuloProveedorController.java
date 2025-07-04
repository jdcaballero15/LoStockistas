package com.stockistas.stockistas2025.Controller;

import com.stockistas.stockistas2025.Dto.ArticuloProveedorDTO;
import com.stockistas.stockistas2025.Entity.ArticuloProveedor;
import com.stockistas.stockistas2025.Repository.ArticuloRepository;
import com.stockistas.stockistas2025.Service.ArticuloProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articulos-proveedores")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ArticuloProveedorController {

    private final ArticuloProveedorService service;
    private final ArticuloRepository articuloRepository;

    //-----------------------------------------------------------------------------------------------
    // Crea una relación entre un artículo y un proveedor, a partir del DTO recibido
    @PostMapping("/{codProveedor}")
    public ResponseEntity<ArticuloProveedor> crearRelacion(
            @PathVariable Integer codProveedor,
            @RequestBody ArticuloProveedorDTO dto
    ) {
        ArticuloProveedor ap = service.agregarRelacion(dto, codProveedor);
        return ResponseEntity.ok(ap);
    }

    //-----------------------------------------------------------------------------------------------
    // Elimina la relación entre un artículo y un proveedor específico
    @DeleteMapping("/{codProveedor}/{codArticulo}")
    public ResponseEntity<Void> eliminarRelacion(
            @PathVariable Integer codProveedor,
            @PathVariable Integer codArticulo
    ) {
        service.eliminarRelacion(codProveedor, codArticulo);
        return ResponseEntity.ok(null);
    }

    //-----------------------------------------------------------------------------------------------
    // Obtiene todas las relaciones entre artículos y proveedores existentes
    @GetMapping
    public ResponseEntity<List<ArticuloProveedor>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }

    //-----------------------------------------------------------------------------------------------
    @GetMapping("/articulo/{codArticulo}")
    public ResponseEntity<List<ArticuloProveedorDTO>> obtenerPorArticulo(@PathVariable Integer codArticulo) {
        return articuloRepository.findById(codArticulo)
                .map(articulo -> {
                    List<ArticuloProveedorDTO> relaciones = service.obtenerPorArticulo(articulo);
                    return ResponseEntity.ok(relaciones);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
