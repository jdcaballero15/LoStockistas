package com.stockistas.stockistas2025;

import com.stockistas.stockistas2025.Dto.VentaDTO;
import com.stockistas.stockistas2025.Entity.*;
import com.stockistas.stockistas2025.Repository.*;
import com.stockistas.stockistas2025.Service.OrdenCompraService;
import com.stockistas.stockistas2025.Service.VentaService;
import jakarta.persistence.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@SpringBootApplication
public class Stockistas2025Application {

	public static void main(String[] args) {
		SpringApplication.run(Stockistas2025Application.class, args);
	}

	@Bean
	public CommandLineRunner demo(ProveedorRepository proveedorRepo, ArticuloRepository articuloRepo, ArticuloProveedorRepository articuloProveedorRepo,
								  EstadoOCRepository estadoOCRepo,
								  OrdenCompraService ordenCompraService,
								  VentaRepository ventaRepo,
								  VentaService ventaService) {
		return args -> {
			System.out.println("Se esta ejecutando Stockistas");

			// Crear los estados
			/*EstadoOC pendiente = EstadoOC
					.builder()
                    .codEstadoOC(1)
                    .nombreEstadoOC("PENDIENTE")
                    .build();*/


			/*EstadoOC enviada = EstadoOC.builder()
					.codEstadoOC(2)
					.nombreEstadoOC("ENVIADA")
					.build();

			EstadoOC cancelada = EstadoOC.builder()
					.codEstadoOC(3)
					.nombreEstadoOC("CANCELADA")
					.build();

			EstadoOC finalizada = EstadoOC.builder()
					.codEstadoOC(4)
					.nombreEstadoOC("FINALIZADA")
					.build();*/

			// Guardar en la base de datos
			//estadoOCRepo.save(enviada);
			//estadoOCRepo.save(cancelada);
			//estadoOCRepo.save(finalizada);
			//estadoOCRepo.save(pendiente);

            // Crear los proveedores

			/*Articulo teclado = Articulo.builder()
					.nombreArt("Teclado Mecánico RGB")
					.descripArt("Teclado mecánico con retroiluminación RGB y switches azules.")
					.demandaAnual(500)
					.costoAlmacenamiento(BigDecimal.valueOf(100))
					.costoPedido(BigDecimal.valueOf(250))
					.costoCompra(BigDecimal.valueOf(8000))
					.stockActual(15)
					.CGI(BigDecimal.ZERO)
					.loteOptimo(20)
					.puntoPedido(10)
					.inventarioMax(40)
					.stockSeguridad(8)
					.urlImagen("https://example.com/teclado.jpg")
					.modeloInventario(ModeloInventario.LOTEFIJO)
					.build();
			articuloRepo.save(teclado);

			Articulo mouse = Articulo.builder()
					.nombreArt("Mouse Inalámbrico")
					.descripArt("Mouse inalámbrico ergonómico con 1600 DPI.")
					.demandaAnual(700)
					.costoAlmacenamiento(BigDecimal.valueOf(80))
					.costoPedido(BigDecimal.valueOf(200))
					.costoCompra(BigDecimal.valueOf(5000))
					.stockActual(5)
					.CGI(BigDecimal.ZERO)
					.loteOptimo(30)
					.puntoPedido(12)
					.inventarioMax(50)
					.stockSeguridad(6)
					.urlImagen("https://example.com/mouse.jpg")
					.modeloInventario(ModeloInventario.LOTEFIJO)
					.build();
			articuloRepo.save(mouse);

			Articulo auriculares = Articulo.builder()
					.nombreArt("Auriculares Bluetooth")
					.descripArt("Auriculares inalámbricos con micrófono y cancelación de ruido.")
					.demandaAnual(300)
					.costoAlmacenamiento(BigDecimal.valueOf(120))
					.costoPedido(BigDecimal.valueOf(220))
					.costoCompra(BigDecimal.valueOf(10000))
					.stockActual(7)
					.CGI(BigDecimal.ZERO)
					.loteOptimo(25)
					.puntoPedido(10)
					.inventarioMax(40)
					.stockSeguridad(5)
					.urlImagen("https://example.com/auriculares.jpg")
					.modeloInventario(ModeloInventario.LOTEFIJO)
					.build();
			articuloRepo.save(auriculares);

			Articulo monitor = Articulo.builder()
					.nombreArt("Monitor 24\" Full HD")
					.descripArt("Monitor LED 24 pulgadas, resolución Full HD, HDMI/VGA.")
					.demandaAnual(200)
					.costoAlmacenamiento(BigDecimal.valueOf(150))
					.costoPedido(BigDecimal.valueOf(300))
					.costoCompra(BigDecimal.valueOf(45000))
					.stockActual(3)
					.CGI(BigDecimal.ZERO)
					.loteOptimo(10)
					.puntoPedido(5)
					.inventarioMax(20)
					.stockSeguridad(4)
					.urlImagen("https://example.com/monitor.jpg")
					.modeloInventario(ModeloInventario.LOTEFIJO)
					.build();
			articuloRepo.save(monitor);

			Articulo webcam = Articulo.builder()
					.nombreArt("Webcam HD 1080p")
					.descripArt("Webcam con micrófono integrado y calidad Full HD.")
					.demandaAnual(350)
					.costoAlmacenamiento(BigDecimal.valueOf(90))
					.costoPedido(BigDecimal.valueOf(180))
					.costoCompra(BigDecimal.valueOf(7000))
					.stockActual(6)
					.CGI(BigDecimal.ZERO)
					.loteOptimo(15)
					.puntoPedido(7)
					.inventarioMax(25)
					.stockSeguridad(5)
					.urlImagen("https://example.com/webcam.jpg")
					.modeloInventario(ModeloInventario.LOTEFIJO)
					.build();
			articuloRepo.save(webcam);*/
		};
		};
	}

