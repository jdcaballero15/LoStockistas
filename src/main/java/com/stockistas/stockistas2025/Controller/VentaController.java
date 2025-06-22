package com.stockistas.stockistas2025.Controller;

import com.stockistas.stockistas2025.Dto.VentaDTO;
import com.stockistas.stockistas2025.Entity.Venta;
import com.stockistas.stockistas2025.Service.VentaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    //-----------------------------------------------------------------------------------------------
    // Registra una nueva venta para un artículo determinado, actualiza el stock y verifica condiciones
    @PostMapping("/ventas")
    public ResponseEntity<?> registrarVenta(@RequestBody VentaDTO dto) {
        try {
            Venta venta = ventaService.registrarVenta(dto);
            return ResponseEntity.ok(venta);
        } catch (EntityNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //-----------------------------------------------------------------------------------------------
    // Devuelve todas las ventas asociadas a un artículo por su código
    @GetMapping("/articulo/{codArticulo}")
    public ResponseEntity<?> listarVentasPorArticulo(@PathVariable Integer codArticulo) {
        try {
            List<Venta> ventas = ventaService.listarVentasPorArticulo(codArticulo);
            return ResponseEntity.ok(ventas);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    //-----------------------------------------------------------------------------------------------
}
