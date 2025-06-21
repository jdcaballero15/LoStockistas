package com.stockistas.stockistas2025.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImagenServiceTest {

    @Mock private Cloudinary cloudinary;
    @Mock private Uploader uploader;
    @Mock private MultipartFile archivo;

    @InjectMocks private ImagenService imagenService;

    @Test
    void subirImagen_deberiaDevolverUrl() throws Exception {
        byte[] bytes = "imagen falsa".getBytes();

        when(archivo.getBytes()).thenReturn(bytes);
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(eq(bytes), anyMap())).thenReturn(Map.of("secure_url", "http://fake.url"));

        String url = imagenService.subirImagen(archivo);

        assertEquals("http://fake.url", url);
    }
}
