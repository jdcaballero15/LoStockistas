package com.stockistas.stockistas2025.Controller;

import com.stockistas.stockistas2025.Dto.ProveedorDTO;
import com.stockistas.stockistas2025.Entity.Proveedor;
import com.stockistas.stockistas2025.Service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;


    @PostMapping
    public ResponseEntity<Proveedor> crear(@RequestBody ProveedorDTO dto) {
        return ResponseEntity.ok(proveedorService.altaProveedor(dto));
    }

    @GetMapping("/{id}/articulos")
    public ResponseEntity<List<String>> articulosPorProveedor(@PathVariable Integer id) {
        return ResponseEntity.ok(proveedorService.listarArticulosPorProveedor(id));
    }
}