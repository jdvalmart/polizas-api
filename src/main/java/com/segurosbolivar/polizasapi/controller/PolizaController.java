package com.segurosbolivar.polizasapi.controller;

import com.segurosbolivar.polizasapi.domain.EstadoPoliza;
import com.segurosbolivar.polizasapi.domain.TipoPoliza;
import com.segurosbolivar.polizasapi.dto.PolizaResponse;
import com.segurosbolivar.polizasapi.dto.RiesgoResponse;
import com.segurosbolivar.polizasapi.service.PolizaService;
import com.segurosbolivar.polizasapi.service.RiesgoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/polizas")
@RequiredArgsConstructor
public class PolizaController {

    private final PolizaService polizaService;
    private final RiesgoService riesgoService;

    @GetMapping
    public List<PolizaResponse> listar(
            @RequestParam(required = false) TipoPoliza tipo,
            @RequestParam(required = false) EstadoPoliza estado) {
        return polizaService.listar(tipo, estado).stream()
                .map(PolizaResponse::from)
                .toList();
    }

    @GetMapping("/{id}/riesgos")
    public List<RiesgoResponse> listarRiesgos(@PathVariable Long id) {
        return riesgoService.listarPorPoliza(id).stream()
                .map(RiesgoResponse::from)
                .toList();
    }

    @PostMapping("/{id}/renovar")
    public PolizaResponse renovar(@PathVariable Long id) {
        return PolizaResponse.from(polizaService.renovar(id));
    }
}
