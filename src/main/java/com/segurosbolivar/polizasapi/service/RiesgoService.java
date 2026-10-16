package com.segurosbolivar.polizasapi.service;

import com.segurosbolivar.polizasapi.domain.EstadoPoliza;
import com.segurosbolivar.polizasapi.domain.EstadoRiesgo;
import com.segurosbolivar.polizasapi.domain.Poliza;
import com.segurosbolivar.polizasapi.domain.Riesgo;
import com.segurosbolivar.polizasapi.domain.TipoPoliza;
import com.segurosbolivar.polizasapi.exception.BusinessException;
import com.segurosbolivar.polizasapi.repository.PolizaRepository;
import com.segurosbolivar.polizasapi.repository.RiesgoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RiesgoService {

    private final RiesgoRepository riesgoRepository;
    private final PolizaRepository polizaRepository;

    public List<Riesgo> listarPorPoliza(Long polizaId) {
        obtenerPoliza(polizaId);
        return riesgoRepository.findByPolizaId(polizaId);
    }

    @Transactional
    public Riesgo agregar(Long polizaId, String descripcion) {
        Poliza poliza = obtenerPoliza(polizaId);

        if (poliza.getTipo() != TipoPoliza.COLECTIVA) {
            throw new BusinessException("Solo se pueden agregar riesgos a polizas colectivas");
        }
        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new BusinessException("No se pueden agregar riesgos a una poliza cancelada");
        }

        Riesgo riesgo = Riesgo.builder()
                .descripcion(descripcion)
                .poliza(poliza)
                .build();

        return riesgoRepository.save(riesgo);
    }

    public Riesgo cancelar(Long riesgoId) {
        Riesgo riesgo = riesgoRepository.findById(riesgoId)
                .orElseThrow(() -> new NoSuchElementException("Riesgo no encontrado: " + riesgoId));

        if (riesgo.getEstado() == EstadoRiesgo.CANCELADO) {
            throw new BusinessException("El riesgo ya esta cancelado");
        }

        riesgo.setEstado(EstadoRiesgo.CANCELADO);
        return riesgoRepository.save(riesgo);
    }

    private Poliza obtenerPoliza(Long polizaId) {
        return polizaRepository.findById(polizaId)
                .orElseThrow(() -> new NoSuchElementException("Poliza no encontrada: " + polizaId));
    }
}
