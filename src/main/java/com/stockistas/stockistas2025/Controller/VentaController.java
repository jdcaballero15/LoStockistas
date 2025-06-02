package com.stockistas.stockistas2025.Controller;

import com.stockistas.stockistas2025.Dto.VentaDTO;
import com.stockistas.stockistas2025.Entity.Venta;
import com.stockistas.stockistas2025.Service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @PostMapping
    public ResponseEntity<Venta> registrarVenta(@RequestBody VentaDTO dto) {
        return ResponseEntity.ok(ventaService.registrarVenta(dto));
    }



    @GetMapping("/articulo/{codArticulo}")
    public ResponseEntity<List<Venta>> listarPorArticulo(@PathVariable Integer codArticulo) {
        return ResponseEntity.ok(ventaService.listarVentasPorArticulo(codArticulo));
    }
}
