package com.segurosbolivar.polizasapi;

import com.segurosbolivar.polizasapi.service.CoreIntegrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PolizasApiIntegrationTest {

    private static final String API_KEY = "123456";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CoreIntegrationService coreIntegrationService;

    @Test
    void sinApiKeyDevuelve401() throws Exception {
        mockMvc.perform(get("/polizas"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listaPolizasSembradas() throws Exception {
        mockMvc.perform(get("/polizas").header("x-api-key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void renovarPolizaActivaAplicaIpcYNotificaAlCore() throws Exception {
        mockMvc.perform(post("/polizas/1/renovar").header("x-api-key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("RENOVADA"))
                .andExpect(jsonPath("$.canonMensual").value(1132000.00))
                .andExpect(jsonPath("$.prima").value(13584000.00));

        verify(coreIntegrationService).notificar("ACTUALIZACION", 1L);
    }

    @Test
    void noSePuedeRenovarUnaPolizaCancelada() throws Exception {
        mockMvc.perform(post("/polizas/1/cancelar").header("x-api-key", API_KEY))
                .andExpect(status().isOk());

        mockMvc.perform(post("/polizas/1/renovar").header("x-api-key", API_KEY))
                .andExpect(status().isConflict());
    }

    @Test
    void cancelarPolizaCancelaSusRiesgosEnCascada() throws Exception {
        mockMvc.perform(post("/polizas/2/cancelar").header("x-api-key", API_KEY))
                .andExpect(status().isOk());

        mockMvc.perform(get("/polizas/2/riesgos").header("x-api-key", API_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("CANCELADO"))
                .andExpect(jsonPath("$[1].estado").value("CANCELADO"));
    }

    @Test
    void agregarRiesgoAColectivaDevuelve201() throws Exception {
        mockMvc.perform(post("/polizas/2/riesgos")
                        .header("x-api-key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descripcion\":\"Bodega 7\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    void noSePuedeAgregarRiesgoAIndividual() throws Exception {
        mockMvc.perform(post("/polizas/1/riesgos")
                        .header("x-api-key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descripcion\":\"X\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void descripcionVaciaDevuelve400() throws Exception {
        mockMvc.perform(post("/polizas/2/riesgos")
                        .header("x-api-key", API_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descripcion\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void noSePuedeCancelarUnRiesgoDosVeces() throws Exception {
        mockMvc.perform(post("/riesgos/1/cancelar").header("x-api-key", API_KEY))
                .andExpect(status().isOk());

        mockMvc.perform(post("/riesgos/1/cancelar").header("x-api-key", API_KEY))
                .andExpect(status().isConflict());
    }
}
