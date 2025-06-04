package com.stockistas.stockistas2025.Service;

import com.stockistas.stockistas2025.Entity.EstadoOC;
import com.stockistas.stockistas2025.Entity.OrdenCompra;
import com.stockistas.stockistas2025.Repository.EstadoOCRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadoOCService {

    private final EstadoOCRepository estadoOCRepository;
    public List<EstadoOC> getAll() {
        return estadoOCRepository.findAll();
    }
}
