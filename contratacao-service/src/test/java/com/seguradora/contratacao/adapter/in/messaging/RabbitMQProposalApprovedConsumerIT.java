package com.seguradora.contratacao.adapter.in.messaging;

import com.seguradora.contratacao.domain.port.out.PropostaClientPort;
import com.seguradora.contratacao.domain.model.Result;
import com.seguradora.contratacao.adapter.out.persistence.jpa.SpringDataContratacaoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class RabbitMQProposalApprovedConsumerIT {

    @Autowired
    private RabbitMQProposalApprovedConsumer consumer;

    @Autowired
    private SpringDataContratacaoRepository repository;

    @MockBean
    private PropostaClientPort propostaClientPort;

    @Test
    void consumirPropostaAprovada_DeveSalvarNoBancoEChamarClient() {
        // Arrange
        Long propostaId = 42L;
        PropostaAprovadaEvent event = new PropostaAprovadaEvent(propostaId);
        
        when(propostaClientPort.marcarComoContratada(propostaId)).thenReturn(Result.success(null));

        // Act
        consumer.consumirPropostaAprovada(event, "test-correlation-id");

        // Assert
        verify(propostaClientPort, times(1)).marcarComoContratada(propostaId);
        
        // Verifica se persistiu no banco de dados local do contratacao-service
        var contratacoes = repository.findAll();
        assertEquals(1, contratacoes.size());
        assertEquals(propostaId, contratacoes.get(0).getPropostaId());
    }
}
