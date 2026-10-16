package com.segurosbolivar.polizasapi.service;

import com.segurosbolivar.polizasapi.domain.Poliza;
import com.segurosbolivar.polizasapi.domain.Riesgo;
import com.segurosbolivar.polizasapi.repository.PolizaRepository;
import com.segurosbolivar.polizasapi.repository.RiesgoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    private Poliza obtenerPoliza(Long polizaId) {
        return polizaRepository.findById(polizaId)
                .orElseThrow(() -> new NoSuchElementException("Poliza no encontrada: " + polizaId));
    }
}
