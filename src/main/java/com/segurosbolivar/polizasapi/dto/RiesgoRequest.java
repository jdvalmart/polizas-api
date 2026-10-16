package com.segurosbolivar.polizasapi.dto;

import jakarta.validation.constraints.NotBlank;

public record RiesgoRequest(
        @NotBlank(message = "La descripcion es obligatoria")
        String descripcion
) {
}
