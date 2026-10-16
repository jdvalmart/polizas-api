package com.segurosbolivar.polizasapi.config;

import com.segurosbolivar.polizasapi.domain.*;
import com.segurosbolivar.polizasapi.repository.PolizaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {

    private final PolizaRepository polizaRepository;

    @Bean
    CommandLineRunner seed() {
        return args -> {
            if (polizaRepository.count() > 0) {
                return;
            }

            Poliza individual = Poliza.builder()
                    .tipo(TipoPoliza.INDIVIDUAL)
                    .estado(EstadoPoliza.ACTIVA)
                    .fechaInicio(LocalDate.of(2025, 1, 1))
                    .fechaFin(LocalDate.of(2025, 12, 31))
                    .mesesVigencia(12)
                    .canonMensual(new BigDecimal("1000000.00"))
                    .prima(new BigDecimal("12000000.00"))
                    .build();
            individual.agregarRiesgo(Riesgo.builder()
                    .descripcion("Apartamento 101 - Bogota")
                    .build());
            polizaRepository.save(individual);

            Poliza colectiva = Poliza.builder()
                    .tipo(TipoPoliza.COLECTIVA)
                    .estado(EstadoPoliza.ACTIVA)
                    .fechaInicio(LocalDate.of(2025, 3, 1))
                    .fechaFin(LocalDate.of(2026, 2, 28))
                    .mesesVigencia(12)
                    .canonMensual(new BigDecimal("2500000.00"))
                    .prima(new BigDecimal("30000000.00"))
                    .build();
            colectiva.agregarRiesgo(Riesgo.builder()
                    .descripcion("Casa 12 - Medellin")
                    .build());
            colectiva.agregarRiesgo(Riesgo.builder()
                    .descripcion("Local 3 - Cali")
                    .build());
            polizaRepository.save(colectiva);
        };
    }
}
