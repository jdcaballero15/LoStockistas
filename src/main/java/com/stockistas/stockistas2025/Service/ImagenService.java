package com.stockistas.stockistas2025.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class ImagenService {

    private final Cloudinary cloudinary;

    //-----------------------------------------------------------------------------------------------
    // Inyecto el bean de Cloudinary
    public ImagenService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    //-----------------------------------------------------------------------------------------------
    //Sube el MultipartFile a Cloudinary y devuelve la URL pública.
    public String subirImagen(MultipartFile archivo) throws IOException {
        //Convierte a bytes y sube
        Map<?, ?> resultado = cloudinary.uploader()
                .upload(archivo.getBytes(), ObjectUtils.emptyMap());
        return resultado.get("secure_url").toString();
    }

    //-----------------------------------------------------------------------------------------------
}
