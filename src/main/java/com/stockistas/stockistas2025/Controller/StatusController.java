package com.stockistas.stockistas2025.Controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@CrossOrigin(origins = "*")
@RestController
public class StatusController {

    @GetMapping("/ping")
    public String ping() {
        return "LoStockistas backend activo";
    }
}