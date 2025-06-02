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
			/*Proveedor proveedor = Proveedor.builder()
					.nombreProveedor("Distribuciones Argentinas S.A.")
					.direccionProveedor("Av. Corrientes 1234, Buenos Aires")
					.telefonoProveedor("+54 11 4567-8910")
					.emailProveedor("contacto@distrarg.com")
					.fechaHoraBajaProveedor(null)
					.build();

			// Primero guardás el proveedor
			Proveedor proveedorGuardado = proveedorRepo.save(proveedor);

			Articulo articulo = Articulo.builder()
					.nombreArt("Auriculares Mchose")
					.descripArt("Tornillo cabeza Philips de 4mm, acero inoxidable")
					.demandaAnual(1200)
					.costoAlmacenamiento(new BigDecimal("0.50"))
					.costoPedido(new BigDecimal("15.00"))
					.costoCompra(new BigDecimal("0.75"))
					.stockActual(150)
					.fechaHoraBajaArticulo(null)
					.CGI(new BigDecimal("600.00"))
					.loteOptimo(160)
					.puntoPedido(200)
					.inventarioMax(600)
					.stockSeguridadLF(50)
					.stockSeguridadIF(30)
					.modeloInventario(ModeloInventario.LOTEFIJO)
					.proveedorPredeterminado(proveedorGuardado)
					.build();

			articuloRepo.save(articulo); // Guardás el artículo

			// Crear relación Artículo-Proveedor
			ArticuloProveedor relacion = articuloProveedorRepo.save(
					ArticuloProveedor.builder()
							.articulo(articulo)
							.proveedor(proveedor)
							.precioUnitario(new BigDecimal("0.75"))
							.cargosPedido(new BigDecimal("0.10"))
							.build()
			);

			EstadoOC estadoPendiente = estadoOCRepo.save(
					EstadoOC.builder()
							.codEstadoOC(1)
							.nombreEstadoOC("PENDIENTE")
							.build()
			);*/

			Articulo prueba1 = articuloRepo.findById(2)
					.orElseThrow(() -> new EntityNotFoundException("Artículo con ID 2 no encontrado"));

			/*// Ejecutar metodo de generación automática
			Optional<OrdenCompra> ocGenerada = ordenCompraService.generarOrdenCompraSiCorresponde(prueba1.getCodArticulo());

			if (ocGenerada.isPresent()) {
				System.out.println("✅ Orden de Compra generada con ID: " + ocGenerada.get().getNumOC());
			} else {
				System.out.println("⚠ No se generó ninguna Orden de Compra.");
			}*/

			Venta ventaPrueba = ventaRepo.save(
					Venta.builder()
							.fechaVenta(LocalDateTime.now())
							.cantProducto(10)
							.articulo(prueba1)
							.build()
			);

			//ventaService.registrarVenta(ventaPrueba);

		};
		};
	}

