package com.stockistas.stockistas2025.Service;

import com.stockistas.stockistas2025.Dto.VentaDTO;
import com.stockistas.stockistas2025.Entity.Articulo;
import com.stockistas.stockistas2025.Entity.Venta;
import com.stockistas.stockistas2025.Repository.ArticuloRepository;
import com.stockistas.stockistas2025.Repository.VentaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock private ArticuloRepository articuloRepository;
    @Mock private VentaRepository ventaRepository;
    @Mock private OrdenCompraService ordenCompraService;

    @InjectMocks private VentaService ventaService;

    @Test
    void registrarVenta_conStockDisponible_deberiaRegistrarVentaYReducirStock() {
        Articulo articulo = Articulo.builder().codArticulo(1).stockActual(10).build();
        VentaDTO dto = VentaDTO.builder().codArticulo(1).cantidadVendida(3).build();

        when(articuloRepository.findById(1)).thenReturn(Optional.of(articulo));
        when(ventaRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Venta venta = ventaService.registrarVenta(dto);

        assertNotNull(venta);
        assertEquals(7, articulo.getStockActual());
        verify(ordenCompraService).generarOrdenCompraSiCorresponde(1);
    }
}