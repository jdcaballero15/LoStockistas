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


    @GetMapping
    public ResponseEntity<List<OrdenCompra>> getAll() {
        return ResponseEntity.ok(ordenCompraService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenCompra> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ordenCompraService.getById(id));
    }

    @GetMapping("/articulo/{codArticulo}")
    public ResponseEntity<List<OrdenCompra>> getOrdenesCompraByArticulo(@PathVariable Integer codArticulo) {
        List<OrdenCompra> ordenes = ordenCompraService.getOrdenesCompraByArticulo(codArticulo);

        if (ordenes.isEmpty()) {
            // Si no se encuentran órdenes, devuelve un estado 204 No Content.
            // Esto es una buena práctica cuando la respuesta es un "no hay datos".
            return ResponseEntity.noContent().build();
        }
        // Si se encuentran órdenes, devuelve un estado 200 OK y la lista de órdenes.
        return ResponseEntity.ok(ordenes);
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