package com.segurosbolivar.polizasapi.service;

import com.segurosbolivar.polizasapi.domain.EstadoPoliza;
import com.segurosbolivar.polizasapi.domain.Poliza;
import com.segurosbolivar.polizasapi.domain.TipoPoliza;
import com.segurosbolivar.polizasapi.repository.PolizaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PolizaService {

    private final PolizaRepository polizaRepository;

    public List<Poliza> listar(TipoPoliza tipo, EstadoPoliza estado) {
        return polizaRepository.listar(tipo, estado);
    }
}
