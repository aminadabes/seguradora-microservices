package com.seguradora.contratacao.domain.service;

import com.seguradora.contratacao.domain.model.Contratacao;
import com.seguradora.contratacao.domain.model.Result;
import com.seguradora.contratacao.domain.port.out.ContratacaoRepositoryPort;
import com.seguradora.contratacao.domain.port.out.PropostaClientPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContratacaoDomainServiceTest {

    @Mock
    private ContratacaoRepositoryPort repositoryPort;

    @Mock
    private PropostaClientPort propostaClientPort;

    @InjectMocks
    private ContratacaoDomainService domainService;

    @Test
    void efetivar_DevePersistirContratacaoEAtualizarPropostaComSucesso() {
        // Arrange
        Long propostaId = 10L;
        when(repositoryPort.salvar(any(Contratacao.class))).thenAnswer(invocation -> {
            Contratacao c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });
        when(propostaClientPort.marcarComoContratada(propostaId)).thenReturn(Result.success(null));

        // Act
        Result<Contratacao> result = domainService.efetivar(propostaId);

        // Assert
        assertTrue(result.isSuccess());
        assertNotNull(result.getValue());
        assertEquals(1L, result.getValue().getId());
        assertEquals(propostaId, result.getValue().getPropostaId());
        verify(repositoryPort, times(1)).salvar(any(Contratacao.class));
        verify(propostaClientPort, times(1)).marcarComoContratada(propostaId);
    }

    @Test
    void efetivar_DeveRetornarErroSeAtualizacaoDoPropostaServiceFalhar() {
        // Arrange
        Long propostaId = 10L;
        when(repositoryPort.salvar(any(Contratacao.class))).thenAnswer(invocation -> {
            Contratacao c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });
        when(propostaClientPort.marcarComoContratada(propostaId)).thenReturn(Result.failure("Serviço indisponível"));

        // Act
        Result<Contratacao> result = domainService.efetivar(propostaId);

        // Assert
        assertTrue(result.isFailure());
        assertTrue(result.getError().contains("Não foi possível marcar a proposta como contratada"));
        verify(repositoryPort, times(1)).salvar(any(Contratacao.class));
        verify(propostaClientPort, times(1)).marcarComoContratada(propostaId);
    }
}
