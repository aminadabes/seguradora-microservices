package com.seguradora.proposta.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seguradora.proposta.adapter.in.rest.dto.PropostaRequest;
import com.seguradora.proposta.domain.port.out.PropostaEventPublisherPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PropostaControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mock do publisher para evitar conexão com o RabbitMQ real nos testes de integração
    @MockBean
    private PropostaEventPublisherPort eventPublisherPort;

    @Test
    void criarEExecutarFluxoDeProposta() throws Exception {
        // 1. Criar Proposta
        PropostaRequest request = new PropostaRequest();
        request.setValor(5000.0);
        request.setClienteNome("José da Silva");
        request.setClienteCpf("111.222.333-44");

        String responseJson = mockMvc.perform(post("/api/propostas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", notNullValue()))
                .andExpect(jsonPath("$.data.status", is("EM_ANALISE")))
                .andExpect(jsonPath("$.data.clienteNome", is("José da Silva")))
                .andReturn().getResponse().getContentAsString();

        // Extrair ID da resposta
        Number idObj = objectMapper.readTree(responseJson).path("data").path("id").numberValue();
        Long propostaId = idObj.longValue();

        // 1.1. Consultar Proposta por ID
        mockMvc.perform(get("/api/propostas/{id}", propostaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", is(propostaId.intValue())))
                .andExpect(jsonPath("$.data.clienteNome", is("José da Silva")));

        // 1.2. Consultar Proposta Inexistente por ID
        mockMvc.perform(get("/api/propostas/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error", containsString("Proposta não encontrada")));

        // 2. Aprovar Proposta
        mockMvc.perform(put("/api/propostas/{id}/aprovar", propostaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("APROVADA")));

        // 3. Contratar Proposta (Chamada interna simulada)
        mockMvc.perform(put("/api/propostas/{id}/contratar", propostaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.status", is("CONTRATADA")));
    }

    @Test
    void criarProposta_ComDadosInvalidos_DeveRetornarErroDeValidao() throws Exception {
        PropostaRequest request = new PropostaRequest();
        request.setValor(-100.0); // Inválido
        request.setClienteNome(""); // Inválido
        request.setClienteCpf(""); // Inválido

        mockMvc.perform(post("/api/propostas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.error", containsString("Erro de validação")));
    }
}
