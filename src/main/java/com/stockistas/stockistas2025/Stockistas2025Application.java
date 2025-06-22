package com.stockistas.stockistas2025;

import com.stockistas.stockistas2025.Repository.*;
import com.stockistas.stockistas2025.Service.OrdenCompraService;
import com.stockistas.stockistas2025.Service.VentaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
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

		};
		};
	}

