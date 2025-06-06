package com.stockistas.stockistas2025.Controller;

import com.stockistas.stockistas2025.Dto.ArticuloDTO;
import com.stockistas.stockistas2025.Entity.Articulo;
import com.stockistas.stockistas2025.Service.ArticuloService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articulos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ArticuloController {

    private final ArticuloService articuloService;

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
}
