package com.stockistas.stockistas2025.Controller;

import com.stockistas.stockistas2025.Dto.ArticuloProveedorDTO;
import com.stockistas.stockistas2025.Entity.ArticuloProveedor;
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

    @PostMapping("/{codProveedor}")
    public ResponseEntity<ArticuloProveedor> crearRelacion(
            @PathVariable Integer codProveedor,
            @RequestBody ArticuloProveedorDTO dto
    ) {
        ArticuloProveedor ap = service.agregarRelacion(dto, codProveedor);
        return ResponseEntity.ok(ap);
    }

    @GetMapping
    public ResponseEntity<List<ArticuloProveedor>> obtenerTodos() {
        return ResponseEntity.ok(service.obtenerTodos());
    }
}

