package com.seguradora.proposta.domain.service;

import com.seguradora.proposta.domain.model.Proposta;
import com.seguradora.proposta.domain.model.PropostaStatus;
import com.seguradora.proposta.domain.model.Result;
import com.seguradora.proposta.domain.port.out.PropostaEventPublisherPort;
import com.seguradora.proposta.domain.port.out.PropostaRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropostaDomainServiceTest {

    @Mock
    private PropostaRepositoryPort repositoryPort;

    @Mock
    private PropostaEventPublisherPort eventPublisherPort;

    @InjectMocks
    private PropostaDomainService domainService;

    private Proposta propostaEmAnalise;

    @BeforeEach
    void setUp() {
        propostaEmAnalise = Proposta.builder()
                .id(1L)
                .valor(1500.0)
                .clienteNome("João Silva")
                .clienteCpf("123.456.789-00")
                .status(PropostaStatus.EM_ANALISE)
                .build();
    }

    @Test
    void criar_DeveSalvarComStatusEmAnalise() {
        // Arrange
        Proposta novaProposta = Proposta.builder()
                .valor(1000.0)
                .clienteNome("Maria Souza")
                .clienteCpf("987.654.321-11")
                .build();

        when(repositoryPort.salvar(any(Proposta.class))).thenAnswer(invocation -> {
            Proposta p = invocation.getArgument(0);
            p.setId(2L);
            return p;
        });

        // Act
        Result<Proposta> result = domainService.criar(novaProposta);

        // Assert
        assertTrue(result.isSuccess());
        assertNotNull(result.getValue());
        assertEquals(2L, result.getValue().getId());
        assertEquals(PropostaStatus.EM_ANALISE, result.getValue().getStatus());
        assertNotNull(result.getValue().getDataCriacao());
        verify(repositoryPort, times(1)).salvar(any(Proposta.class));
    }

    @Test
    void aprovar_DeveMudarStatusParaAprovadaEPublicarEvento() {
        // Arrange
        when(repositoryPort.buscarPorId(1L)).thenReturn(Optional.of(propostaEmAnalise));
        when(repositoryPort.salvar(any(Proposta.class))).thenReturn(propostaEmAnalise);

        // Act
        Result<Proposta> result = domainService.aprovar(1L);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(PropostaStatus.APROVADA, result.getValue().getStatus());
        verify(repositoryPort, times(1)).salvar(propostaEmAnalise);
        verify(eventPublisherPort, times(1)).publicarPropostaAprovada(propostaEmAnalise);
    }

    @Test
    void aprovar_NaoDeveAprovarSePropostaNaoExistir() {
        // Arrange
        when(repositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        // Act
        Result<Proposta> result = domainService.aprovar(99L);

        // Assert
        assertTrue(result.isFailure());
        assertTrue(result.getError().contains("não encontrada"));
        verify(repositoryPort, never()).salvar(any());
        verify(eventPublisherPort, never()).publicarPropostaAprovada(any());
    }

    @Test
    void aprovar_NaoDeveAprovarSePropostaNaoEstiverEmAnalise() {
        // Arrange
        propostaEmAnalise.setStatus(PropostaStatus.CONTRATADA);
        when(repositoryPort.buscarPorId(1L)).thenReturn(Optional.of(propostaEmAnalise));

        // Act
        Result<Proposta> result = domainService.aprovar(1L);

        // Assert
        assertTrue(result.isFailure());
        assertEquals("Apenas propostas EM ANALISE podem ser aprovadas.", result.getError());
        verify(repositoryPort, never()).salvar(any());
        verify(eventPublisherPort, never()).publicarPropostaAprovada(any());
    }

    @Test
    void contratar_DeveMudarStatusParaContratada() {
        // Arrange
        propostaEmAnalise.setStatus(PropostaStatus.APROVADA);
        when(repositoryPort.buscarPorId(1L)).thenReturn(Optional.of(propostaEmAnalise));
        when(repositoryPort.salvar(any(Proposta.class))).thenReturn(propostaEmAnalise);

        // Act
        Result<Proposta> result = domainService.contratar(1L);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals(PropostaStatus.CONTRATADA, result.getValue().getStatus());
        verify(repositoryPort, times(1)).salvar(propostaEmAnalise);
    }
}
