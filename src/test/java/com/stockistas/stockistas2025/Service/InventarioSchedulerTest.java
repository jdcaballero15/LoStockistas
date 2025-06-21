package com.stockistas.stockistas2025.Service;

import com.stockistas.stockistas2025.Entity.ModeloInventario;
import com.stockistas.stockistas2025.Repository.ArticuloRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventarioSchedulerTest {

    @Mock private ArticuloRepository articuloRepository;
    @Mock private OrdenCompraService ordenCompraService;

    @InjectMocks private InventarioScheduler scheduler;

    @Test
    void verificarStockIntervaloFijo_deberiaConsultarArticulos() {
        when(articuloRepository.findByModeloInventario(ModeloInventario.INTERVALOFIJO))
                .thenReturn(Collections.emptyList());

        scheduler.verificarStockIntervaloFijo();

        verify(articuloRepository).findByModeloInventario(ModeloInventario.INTERVALOFIJO);
    }
}