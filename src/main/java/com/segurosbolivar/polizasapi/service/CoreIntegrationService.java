package com.segurosbolivar.polizasapi.service;

import com.segurosbolivar.polizasapi.dto.CoreEventoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class CoreIntegrationService {

    private final RestClient restClient;
    private final String apiKey;

    public CoreIntegrationService(
            @Value("${app.core.url}") String coreUrl,
            @Value("${app.api-key}") String apiKey) {
        this.restClient = RestClient.builder().baseUrl(coreUrl).build();
        this.apiKey = apiKey;
    }

    public void notificar(String evento, Long polizaId) {
        CoreEventoRequest request = new CoreEventoRequest(evento, polizaId);
        log.info("Enviando evento al CORE: {}", request);
        try {
            restClient.post()
                    .header("x-api-key", apiKey)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception ex) {
            log.warn("No se pudo notificar al CORE (evento={}, polizaId={}): {}",
                    evento, polizaId, ex.getMessage());
        }
    }
}
