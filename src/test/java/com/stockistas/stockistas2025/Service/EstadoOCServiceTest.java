package com.stockistas.stockistas2025.Service;

import com.stockistas.stockistas2025.Repository.EstadoOCRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EstadoOCServiceTest {

    @Mock private EstadoOCRepository estadoOCRepository;

    @InjectMocks private EstadoOCService estadoOCService;

    @Test
    void getAll_deberiaInvocarFindAll() {
        estadoOCService.getAll();
        verify(estadoOCRepository).findAll();
    }
}