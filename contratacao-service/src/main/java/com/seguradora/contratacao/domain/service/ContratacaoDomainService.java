package com.seguradora.contratacao.domain.service;

import com.seguradora.contratacao.domain.model.Contratacao;
import com.seguradora.contratacao.domain.model.Result;
import com.seguradora.contratacao.domain.port.in.EfetivarContratacaoUseCase;
import com.seguradora.contratacao.domain.port.out.ContratacaoRepositoryPort;
import com.seguradora.contratacao.domain.port.out.PropostaClientPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContratacaoDomainService implements EfetivarContratacaoUseCase {

    private final ContratacaoRepositoryPort repositoryPort;
    private final PropostaClientPort propostaClientPort;

    @Override
    @Transactional
    public Result<Contratacao> efetivar(Long propostaId) {
        log.info("Efetivando contratação para a proposta ID: {}", propostaId);

        // 1. Cria a contratação e persiste no banco próprio
        Contratacao contratacao = Contratacao.builder()
                .propostaId(propostaId)
                .dataContratacao(LocalDateTime.now())
                .build();

        Contratacao contratacaoSalva = repositoryPort.salvar(contratacao);
        log.info("Contratação salva localmente com ID: {}. Solicitando alteração de status na proposta...", contratacaoSalva.getId());

        // 2. Faz a chamada interna ao PropostaService para efetivar
        Result<Void> apiResult = propostaClientPort.marcarComoContratada(propostaId);
        if (apiResult.isFailure()) {
            log.error("Falha ao atualizar status da proposta ID: {} no PropostaService. Motivo: {}", propostaId, apiResult.getError());
            return Result.failure("Não foi possível marcar a proposta como contratada no serviço de proposta: " + apiResult.getError());
        }

        log.info("Contratação finalizada com sucesso para a proposta ID: {}", propostaId);
        return Result.success(contratacaoSalva);
    }
}
