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
@RequestMapping("/api/estados-oc")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EstadoOCController {

    private final EstadoOCService estadoOCService;

    //-----------------------------------------------------------------------------------------------
    //Devuelve todos los estados
    @GetMapping("")
    public ResponseEntity<List<EstadoOC>> estadosOC(){
        return ResponseEntity.ok(estadoOCService.getAll());
    }

    //-----------------------------------------------------------------------------------------------
}
