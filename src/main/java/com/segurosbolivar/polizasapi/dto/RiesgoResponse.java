package com.segurosbolivar.polizasapi.dto;

import com.segurosbolivar.polizasapi.domain.EstadoRiesgo;
import com.segurosbolivar.polizasapi.domain.Riesgo;

public record RiesgoResponse(
        Long id,
        Long polizaId,
        String descripcion,
        EstadoRiesgo estado
) {

    public static RiesgoResponse from(Riesgo riesgo) {
        return new RiesgoResponse(
                riesgo.getId(),
                riesgo.getPoliza().getId(),
                riesgo.getDescripcion(),
                riesgo.getEstado()
        );
    }
}
