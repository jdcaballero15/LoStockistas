package com.stockistas.stockistas2025.Service;

import com.stockistas.stockistas2025.Dto.ArticuloProveedorDTO;
import com.stockistas.stockistas2025.Entity.Articulo;
import com.stockistas.stockistas2025.Entity.ArticuloProveedor;
import com.stockistas.stockistas2025.Entity.Proveedor;
import com.stockistas.stockistas2025.Repository.ArticuloProveedorRepository;
import com.stockistas.stockistas2025.Repository.ArticuloRepository;
import com.stockistas.stockistas2025.Repository.ProveedorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticuloProveedorServiceTest {

    @Mock private ArticuloProveedorRepository articuloProveedorRepo;
    @Mock private ArticuloRepository articuloRepo;
    @Mock private ProveedorRepository proveedorRepo;

    @InjectMocks private ArticuloProveedorService service;

    @Test
    void agregarRelacion_deberiaGuardarRelacion() {
        Articulo articulo = Articulo.builder().codArticulo(1).build();
        Proveedor proveedor = Proveedor.builder().codProveedor(1).build();
        ArticuloProveedorDTO dto = new ArticuloProveedorDTO(1, BigDecimal.TEN, BigDecimal.ONE, 3);

        when(articuloRepo.findById(1)).thenReturn(Optional.of(articulo));
        when(proveedorRepo.findById(1)).thenReturn(Optional.of(proveedor));
        when(articuloProveedorRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        ArticuloProveedor ap = service.agregarRelacion(dto, 1);
        assertNotNull(ap);
        assertEquals(articulo, ap.getArticulo());
        assertEquals(proveedor, ap.getProveedor());
    }
}