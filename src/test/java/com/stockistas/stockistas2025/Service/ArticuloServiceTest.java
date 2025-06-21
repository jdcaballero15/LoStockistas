package com.stockistas.stockistas2025.Service;

import com.stockistas.stockistas2025.Repository.ArticuloProveedorRepository;
import com.stockistas.stockistas2025.Repository.ArticuloRepository;
import com.stockistas.stockistas2025.Repository.OrdenCompraRepository;
import com.stockistas.stockistas2025.Repository.ProveedorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ArticuloServiceTest {

    @Mock private ArticuloRepository articuloRepository;
    @Mock private ProveedorRepository proveedorRepository;
    @Mock private OrdenCompraRepository ordenCompraRepository;
    @Mock private ArticuloProveedorRepository articuloProveedorRepository;

    @InjectMocks private ArticuloService articuloService;

    @Test
    void calcularStockSeguridad_deberiaCalcularCorrectamente() {
        Integer resultado = articuloService.calcularStockSeguridad(0.95, 10.0);
        assertEquals(17, resultado);
    }

    @Test
    void calcularStockSeguridad_conNivelInvalido_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
            articuloService.calcularStockSeguridad(1.5, 10.0)
        );
    }
}