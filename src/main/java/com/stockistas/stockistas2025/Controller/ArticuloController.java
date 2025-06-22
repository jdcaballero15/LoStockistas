package com.stockistas.stockistas2025.Controller;

import com.stockistas.stockistas2025.Dto.ArticuloDTO;
import com.stockistas.stockistas2025.Entity.Articulo;
import com.stockistas.stockistas2025.Entity.ModeloInventario;
import com.stockistas.stockistas2025.Entity.Proveedor;
import com.stockistas.stockistas2025.Repository.ArticuloProveedorRepository;
import com.stockistas.stockistas2025.Repository.ArticuloRepository;
import com.stockistas.stockistas2025.Repository.ProveedorRepository;
import com.stockistas.stockistas2025.Service.ArticuloService;
import com.stockistas.stockistas2025.Service.ImagenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/articulos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ArticuloController {

    private final ArticuloService articuloService;
    private final ImagenService imagenService;
    private final ArticuloRepository articuloRepository;

    //-----------------------------------------------------------------------------------------------
    // Devuelve todos los artículos existentes
    @GetMapping
    public ResponseEntity<List<ArticuloDTO>> getAll() {
        return ResponseEntity.ok(articuloService.getAll());
    }

    //-----------------------------------------------------------------------------------------------
    // Devuelve un artículo específico por su ID
    @GetMapping("/{id}")
    public ResponseEntity<Articulo> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(articuloService.getById(id));
    }

    //-----------------------------------------------------------------------------------------------
    // Devuelve los artículos cuyo stock está por debajo del nivel de seguridad
    @GetMapping("/stock-critico")
    public ResponseEntity<List<ArticuloDTO>> obtenerArticulosConStockCritico() {
        return ResponseEntity.ok(articuloService.obtenerArticulosCriticos());
    }

    //-----------------------------------------------------------------------------------------------
    // Crea un nuevo artículo a partir de los datos recibidos en el DTO
    @PostMapping
    public ResponseEntity<Articulo> create(@RequestBody ArticuloDTO dto) {
        return ResponseEntity.ok(articuloService.crearArticulo(dto));
    }

    //-----------------------------------------------------------------------------------------------
    // Crea un nuevo artículo con imagen cargada mediante formulario multipart
    @PostMapping(
            value = "/con-imagen",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Articulo> createConImagen(
            @RequestParam("nombreArt") String nombreArt,
            @RequestParam("descripArt") String descripArt,
            @RequestParam("demandaAnual") Integer demandaAnual,
            @RequestParam("stockActual") Integer stockActual,
            @RequestParam("inventarioMax") Integer inventarioMax,
            @RequestParam("stockSeguridad") Integer stockSeguridad,
            @RequestParam("modeloInventario")  String modeloInventario,
            @RequestParam("archivo")          MultipartFile archivo,
            @RequestParam("nivelServicio")   double nivelServicio,
            @RequestParam("desviacionEstandar")   double desviacionEstandar
    ) throws IOException {

        // 1) Subo la imagen y obtengo la URL
        String urlImagen = imagenService.subirImagen(archivo);
        // 2) Armo el DTO
        ArticuloDTO dto = ArticuloDTO.builder()
                .nombreArt(nombreArt)
                .descripArt(descripArt)
                .demandaAnual(demandaAnual)
                .stockActual(stockActual)
                .inventarioMax(inventarioMax)
                .stockSeguridad(stockSeguridad)
                .modeloInventario(ModeloInventario.valueOf(modeloInventario))
                .nivelServicio(nivelServicio)
                .desviacionEstandar(desviacionEstandar)
                .urlImagen(urlImagen)
                .build();

        // 3) Delego la creación al servicio
        Articulo creado = articuloService.crearArticuloConImagen(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    //-----------------------------------------------------------------------------------------------
    // Actualiza un artículo existente por ID
    @PutMapping("/{id}")
    public ResponseEntity<Articulo> update(
            @PathVariable Integer id,
            @RequestBody ArticuloDTO dto
    ) {
        return ResponseEntity.ok(articuloService.update(id, dto));
    }

    //-----------------------------------------------------------------------------------------------
    // Elimina un artículo por su ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        articuloService.delete(id);
        return ResponseEntity.noContent().build();
    }

    //-----------------------------------------------------------------------------------------------
    // Devuelve los proveedores asociados a un artículo específico
    @GetMapping("/{articuloId}/proveedores")
    public ResponseEntity<List<Proveedor>> getProveedoresByArticulo(@PathVariable Integer articuloId) {

        List<Proveedor> proveedores = articuloService.getProveedoresByArticulo(articuloId);
        return ResponseEntity.ok(proveedores);
    }

    //-----------------------------------------------------------------------------------------------
    //Devuelve artículos que tienen un proveedor predeterminado (para usar en creación manual de orden)
    @GetMapping("/con-proveedor")
    public ResponseEntity<List<ArticuloDTO>> getArticulosConProveedor() {
        List<Articulo> articulos = articuloRepository.findByProveedorPredeterminadoIsNotNull();
        // Convertir cada Articulo a ArticuloDTO
        List<ArticuloDTO> articulosDTO = articulos.stream()
                .map(articuloService::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(articulosDTO);
    }

    //-----------------------------------------------------------------------------------------------
}
