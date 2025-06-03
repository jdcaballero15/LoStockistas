package com.stockistas.stockistas2025.Controller;

import com.stockistas.stockistas2025.Dto.ArticuloProveedorResponseDTO;
import com.stockistas.stockistas2025.Dto.ProveedorDTO;
import com.stockistas.stockistas2025.Dto.ProveedorDTOO;
import com.stockistas.stockistas2025.Entity.Articulo;
import com.stockistas.stockistas2025.Entity.Proveedor;
import com.stockistas.stockistas2025.Service.ProveedorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProveedorController {

    private final ProveedorService proveedorService;

    @PostMapping
    public ResponseEntity<?> crearProveedor(@RequestBody ProveedorDTO dto) {
        try {
            Proveedor nuevoProveedor = proveedorService.altaProveedor(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProveedor);
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProveedor(@PathVariable("id") Integer id) {
        try {
            proveedorService.bajaProveedor(id);
            return ResponseEntity.ok("✅ Proveedor eliminado correctamente.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ " + e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("⚠️ " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("⛔ Error inesperado: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<ProveedorDTOO>> getAll() {
        return ResponseEntity.ok(proveedorService.getAll());
    }


    @GetMapping("/{id}/articulos")
    public ResponseEntity<?> listarArticulosDelProveedor(@PathVariable("id") Integer id) {
        try {
            List<ArticuloProveedorResponseDTO> articulos = proveedorService.listarArticulosPorProveedor(id);
            return ResponseEntity.ok(articulos);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("❌ " + e.getMessage());
        }
    }

}