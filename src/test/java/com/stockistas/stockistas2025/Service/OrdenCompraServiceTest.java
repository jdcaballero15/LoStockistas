package com.stockistas.stockistas2025.Service;

import com.stockistas.stockistas2025.Entity.Articulo;
import com.stockistas.stockistas2025.Entity.ModeloInventario;
import com.stockistas.stockistas2025.Repository.ArticuloRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class OrdenCompraServiceTest {

    @Mock private ArticuloRepository articuloRepository;

    @InjectMocks private OrdenCompraService ordenCompraService;

    @Test
    void calcularFaltanteSegunModelo_loteFijo_deberiaDevolverLote() {
        Articulo articulo = Articulo.builder()
                .modeloInventario(ModeloInventario.LOTEFIJO)
                .stockActual(5)
                .puntoPedido(6)
                .stockSeguridad(2)
                .loteOptimo(10)
                .build();

        int faltante = ordenCompraService.calcularFaltanteSegunModelo(articulo);
        assertEquals(10, faltante);
    }
}