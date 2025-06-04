package com.stockistas.stockistas2025.Controller;

import com.stockistas.stockistas2025.Dto.OrdenCompraDTO;
import com.stockistas.stockistas2025.Entity.EstadoOC;
import com.stockistas.stockistas2025.Entity.OrdenCompra;
import com.stockistas.stockistas2025.Service.EstadoOCService;
import com.stockistas.stockistas2025.Service.OrdenCompraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrdenCompraController {

    private final OrdenCompraService ordenCompraService;
    private final EstadoOCService estadoOCService;

    @GetMapping
    public ResponseEntity<List<OrdenCompra>> getAll() {
        return ResponseEntity.ok(ordenCompraService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenCompra> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ordenCompraService.getById(id));
    }

    @GetMapping("/{estados-oc}")
    public ResponseEntity<List<EstadoOC>>estadosOC(){
        return ResponseEntity.ok(estadoOCService.getAll());
    }

    // TENEMOS QUE HACERLO EN EL FRONT
    @PostMapping
    public ResponseEntity<OrdenCompra> create(@RequestBody OrdenCompraDTO dto) {
        return ResponseEntity.ok(ordenCompraService.crearOrdenCompraManual(dto));
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<String> finalizar(@PathVariable Integer id) {
        ordenCompraService.finalizarOrdenCompra(id);
        return ResponseEntity.ok("Orden de compra finalizada exitosamente");
    }

    @DeleteMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Integer id) {
        ordenCompraService.cancelarOrdenCompra(id);
        return ResponseEntity.noContent().build();
    }


}