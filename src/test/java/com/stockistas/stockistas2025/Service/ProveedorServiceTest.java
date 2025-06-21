package com.stockistas.stockistas2025.Service;

import com.stockistas.stockistas2025.Dto.ArticuloProveedorDTO;
import com.stockistas.stockistas2025.Dto.ProveedorDTO;
import com.stockistas.stockistas2025.Entity.Articulo;
import com.stockistas.stockistas2025.Entity.Proveedor;
import com.stockistas.stockistas2025.Repository.ArticuloProveedorRepository;
import com.stockistas.stockistas2025.Repository.ArticuloRepository;
import com.stockistas.stockistas2025.Repository.OrdenCompraRepository;
import com.stockistas.stockistas2025.Repository.ProveedorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProveedorServiceTest {

    @Mock private ProveedorRepository proveedorRepository;
    @Mock private ArticuloRepository articuloRepository;
    @Mock private ArticuloProveedorRepository articuloProveedorRepository;
    @Mock private OrdenCompraRepository ordenCompraRepository;

    @InjectMocks private ProveedorService proveedorService;

    @Test
    void altaProveedor_deberiaGuardarProveedorConArticulo() {
        Articulo articulo = Articulo.builder().codArticulo(1).build();
        Proveedor proveedor = Proveedor.builder().codProveedor(1).build();
        ArticuloProveedorDTO ap = new ArticuloProveedorDTO(1, new BigDecimal("10.0"), new BigDecimal("2.0"), 5);
        ProveedorDTO dto = ProveedorDTO.builder()
                .nombre("Test Proveedor")
                .correo("mail@test.com")
                .direccion("calle 123")
                .telefono("123456")
                .intervaloReposicion(10)
                .articulos(List.of(ap))
                .build();

        when(articuloRepository.findById(1)).thenReturn(Optional.of(articulo));
        when(proveedorRepository.save(any())).thenReturn(proveedor);

        Proveedor resultado = proveedorService.altaProveedor(dto);

        assertNotNull(resultado);
        verify(articuloProveedorRepository).save(any());
    }
}