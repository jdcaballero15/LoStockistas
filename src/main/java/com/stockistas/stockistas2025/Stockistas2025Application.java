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
					.build();

			// Guardar en la base de datos
			estadoOCRepo.save(enviada);
			estadoOCRepo.save(cancelada);
			estadoOCRepo.save(finalizada);*/
		};
		};
	}

