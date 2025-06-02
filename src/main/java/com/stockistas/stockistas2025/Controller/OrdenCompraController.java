package com.stockistas.stockistas2025.Controller;

import com.stockistas.stockistas2025.Dto.OrdenCompraDTO;
import com.stockistas.stockistas2025.Entity.OrdenCompra;
import com.stockistas.stockistas2025.Service.OrdenCompraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
public class OrdenCompraController {

    private final OrdenCompraService ordenCompraService;

    @PostMapping("/generar")
    public ResponseEntity<OrdenCompra> generarOC(@RequestParam Integer codArticulo) {
        Optional<OrdenCompra> oc = ordenCompraService.generarOrdenCompraSiCorresponde(codArticulo);
        return oc.map(ResponseEntity::ok).orElse(ResponseEntity.noContent().build());
    }

    @PostMapping("/manual")
    public ResponseEntity<OrdenCompra> crearManual(@RequestBody OrdenCompraDTO dto) {
        return ResponseEntity.ok(ordenCompraService.crearOrdenCompraManual(dto));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Integer id) {
        ordenCompraService.cancelarOrdenCompra(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/enviar")
    public ResponseEntity<Void> enviar(@PathVariable Integer id) {
        ordenCompraService.enviarOrdenCompra(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<Void> finalizar(@PathVariable Integer id) {
        ordenCompraService.finalizarOrdenCompra(id);
        return ResponseEntity.ok().build();
    }
}