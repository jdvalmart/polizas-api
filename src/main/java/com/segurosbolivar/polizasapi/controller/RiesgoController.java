package com.segurosbolivar.polizasapi.controller;

import com.segurosbolivar.polizasapi.dto.RiesgoResponse;
import com.segurosbolivar.polizasapi.service.RiesgoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/riesgos")
@RequiredArgsConstructor
public class RiesgoController {

    private final RiesgoService riesgoService;

    @PostMapping("/{id}/cancelar")
    public RiesgoResponse cancelar(@PathVariable Long id) {
        return RiesgoResponse.from(riesgoService.cancelar(id));
    }
}
