package com.segurosbolivar.polizasapi.service;

import com.segurosbolivar.polizasapi.domain.EstadoPoliza;
import com.segurosbolivar.polizasapi.domain.Poliza;
import com.segurosbolivar.polizasapi.domain.TipoPoliza;
import com.segurosbolivar.polizasapi.exception.BusinessException;
import com.segurosbolivar.polizasapi.repository.PolizaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PolizaService {

    private final PolizaRepository polizaRepository;

    @Value("${app.ipc}")
    private BigDecimal ipc;

    public List<Poliza> listar(TipoPoliza tipo, EstadoPoliza estado) {
        return polizaRepository.listar(tipo, estado);
    }

    public Poliza renovar(Long id) {
        Poliza poliza = obtenerPoliza(id);

        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new BusinessException("No se puede renovar una poliza cancelada");
        }

        BigDecimal factor = BigDecimal.ONE.add(ipc);
        BigDecimal nuevoCanon = poliza.getCanonMensual()
                .multiply(factor)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal nuevaPrima = nuevoCanon
                .multiply(BigDecimal.valueOf(poliza.getMesesVigencia()))
                .setScale(2, RoundingMode.HALF_UP);

        poliza.setCanonMensual(nuevoCanon);
        poliza.setPrima(nuevaPrima);
        poliza.setEstado(EstadoPoliza.RENOVADA);

        LocalDate nuevaInicio = poliza.getFechaFin().plusDays(1);
        poliza.setFechaInicio(nuevaInicio);
        poliza.setFechaFin(nuevaInicio.plusMonths(poliza.getMesesVigencia()));

        return polizaRepository.save(poliza);
    }

    private Poliza obtenerPoliza(Long id) {
        return polizaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Poliza no encontrada: " + id));
    }
}
