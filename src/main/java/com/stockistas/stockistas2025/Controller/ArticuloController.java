package com.stockistas.stockistas2025.Controller;

import com.stockistas.stockistas2025.Dto.ArticuloDTO;
import com.stockistas.stockistas2025.Entity.Articulo;
import com.stockistas.stockistas2025.Entity.Proveedor;
import com.stockistas.stockistas2025.Repository.ArticuloProveedorRepository;
import com.stockistas.stockistas2025.Repository.ArticuloRepository;
import com.stockistas.stockistas2025.Service.ArticuloService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/articulos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ArticuloController {

    private final ArticuloService articuloService;
    private final ArticuloProveedorRepository articuloProveedorRepository;
    private final ArticuloRepository articuloRepository;

    @GetMapping
    public ResponseEntity<List<ArticuloDTO>> getAll() {
        return ResponseEntity.ok(articuloService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Articulo> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(articuloService.getById(id));
    }

    @GetMapping("/stock-critico")
    public ResponseEntity<List<Articulo>> obtenerArticulosConStockCritico() {
        List<Articulo> criticos = articuloService.obtenerArticulosCriticos();
        return ResponseEntity.ok(criticos);
    }

    @PostMapping
    public ResponseEntity<Articulo> create(@RequestBody ArticuloDTO dto ) {
        return ResponseEntity.ok(articuloService.crearArticulo(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Articulo> update(@PathVariable Integer id, @RequestBody ArticuloDTO dto) {
        return ResponseEntity.ok(articuloService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        articuloService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{articuloId}/proveedores")
    public ResponseEntity<List<Proveedor>> getProveedoresByArticulo(@PathVariable Integer articuloId) {

        List<Proveedor> proveedores = articuloService.getProveedoresByArticulo(articuloId);
        return ResponseEntity.ok(proveedores);
    }
    //USAR PARA EL FILTRO DE LOS ARTICULOS AL MOMENTO DE CREAR UNA ORDEN MANUAL
    @GetMapping("/con-proveedor")
    public ResponseEntity<List<ArticuloDTO>> getArticulosConProveedor() {
        List<Articulo> articulos = articuloRepository.findByProveedorPredeterminadoIsNotNull();
        // Convertir cada Articulo a ArticuloDTO
        List<ArticuloDTO> articulosDTO = articulos.stream()
                .map(articuloService::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(articulosDTO);
    }

}
