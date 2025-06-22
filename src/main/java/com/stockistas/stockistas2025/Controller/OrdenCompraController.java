package com.stockistas.stockistas2025.Controller;

import com.stockistas.stockistas2025.Dto.OrdenCompraDTO;
import com.stockistas.stockistas2025.Entity.OrdenCompra;
import com.stockistas.stockistas2025.Service.OrdenCompraService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrdenCompraController {

    private final OrdenCompraService ordenCompraService;

    //-----------------------------------------------------------------------------------------------
    // Obtiene todas las órdenes de compra existentes
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<OrdenCompra> ordenesCompra = ordenCompraService.getAll();
            if (ordenesCompra.isEmpty()) {
                throw new EntityNotFoundException("No se encontraron órdenes de compra.");
            }
            return ResponseEntity.ok(ordenesCompra);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    //-----------------------------------------------------------------------------------------------
    // Obtiene una orden de compra específica por su ID
    @GetMapping("/{id}")
    public ResponseEntity<OrdenCompra> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(ordenCompraService.getById(id));
    }

    //-----------------------------------------------------------------------------------------------
    // Obtiene todas las órdenes de compra asociadas a un artículo determinado
    @GetMapping("/articulo/{codArticulo}")
    public ResponseEntity<List<OrdenCompra>> getOrdenesCompraByArticulo(@PathVariable Integer codArticulo) {
        List<OrdenCompra> ordenes = ordenCompraService.getOrdenesCompraByArticulo(codArticulo);

        if (ordenes.isEmpty()) {
            // Si no se encuentran órdenes, devuelve un estado 204 No Content.
            return ResponseEntity.noContent().build();
        }
        // Si se encuentran órdenes, devuelve un estado 200 OK con la lista.
        return ResponseEntity.ok(ordenes);
    }

    //-----------------------------------------------------------------------------------------------
    // Crea una nueva orden de compra manualmente
    @PostMapping
    public ResponseEntity<OrdenCompra> create(@RequestBody OrdenCompraDTO dto) {
        return ResponseEntity.ok(ordenCompraService.crearOrdenCompraManual(dto));
    }

    //-----------------------------------------------------------------------------------------------
    // Cambia el estado de una orden de compra a FINALIZADA
    @PutMapping("/{id}/finalizar")
    public ResponseEntity<String> finalizar(@PathVariable Integer id) {
        ordenCompraService.finalizarOrdenCompra(id);
        return ResponseEntity.ok("Orden de compra finalizada exitosamente");
    }

    //-----------------------------------------------------------------------------------------------
    // Cancela una orden de compra (cambia su estado a CANCELADA)
    @DeleteMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Integer id) {
        ordenCompraService.cancelarOrdenCompra(id);
        return ResponseEntity.noContent().build();
    }

    //-----------------------------------------------------------------------------------------------
    // Cambia el estado de una orden de compra a ENVIADA
    @PutMapping("/{id}/enviar")
    public ResponseEntity<String> enviar(@PathVariable Integer id) {
        ordenCompraService.enviarOrdenCompra(id);
        return ResponseEntity.ok("Orden de compra enviada exitosamente");
    }

    //-----------------------------------------------------------------------------------------------
    // Permite editar la cantidad total de artículos en una orden de compra existente
    @PutMapping("/{id}/editar-cantidad")
    public ResponseEntity<OrdenCompra> editarCantidad(@PathVariable Integer id, @RequestParam Integer nuevaCantidad) {
        OrdenCompra actualizada = ordenCompraService.editarCantidadOrdenCompra(id, nuevaCantidad);
        return ResponseEntity.ok(actualizada);
    }

    //-----------------------------------------------------------------------------------------------
}
