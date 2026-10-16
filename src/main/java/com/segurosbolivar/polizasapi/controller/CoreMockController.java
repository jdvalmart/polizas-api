package com.segurosbolivar.polizasapi.controller;

import com.segurosbolivar.polizasapi.dto.CoreEventoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/core-mock")
public class CoreMockController {

    @PostMapping("/evento")
    public ResponseEntity<Void> evento(@RequestBody CoreEventoRequest request) {
        log.info("CORE-MOCK: se intento enviar al CORE -> evento={}, polizaId={}",
                request.evento(), request.polizaId());
        return ResponseEntity.ok().build();
    }
}
