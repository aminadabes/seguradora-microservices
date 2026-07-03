package com.seguradora.proposta.domain.service;

import com.seguradora.proposta.domain.model.Proposta;
import com.seguradora.proposta.domain.model.PropostaStatus;
import com.seguradora.proposta.domain.model.Result;
import com.seguradora.proposta.domain.port.in.AtualizarStatusPropostaUseCase;
import com.seguradora.proposta.domain.port.in.BuscarPropostaUseCase;
import com.seguradora.proposta.domain.port.in.ContratarPropostaUseCase;
import com.seguradora.proposta.domain.port.in.CriarPropostaUseCase;
import com.seguradora.proposta.domain.port.out.PropostaEventPublisherPort;
import com.seguradora.proposta.domain.port.out.PropostaRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropostaDomainService implements CriarPropostaUseCase, AtualizarStatusPropostaUseCase, ContratarPropostaUseCase, BuscarPropostaUseCase {

    private final PropostaRepositoryPort repositoryPort;
    private final PropostaEventPublisherPort eventPublisherPort;

    @Override
    @Transactional
    public Result<Proposta> criar(Proposta proposta) {
        log.info("Processando criação de nova proposta para o cliente: {}", proposta.getClienteNome());
        proposta.setStatus(PropostaStatus.EM_ANALISE);
        proposta.setDataCriacao(LocalDateTime.now());
        proposta.setDataAtualizacao(LocalDateTime.now());
        
        Proposta propostaSalva = repositoryPort.salvar(proposta);
        log.info("Proposta criada com sucesso. ID: {}", propostaSalva.getId());
        return Result.success(propostaSalva);
    }

    @Override
    @Transactional
    public Result<Proposta> aprovar(Long id) {
        log.info("Processando aprovação da proposta ID: {}", id);
        return repositoryPort.buscarPorId(id)
                .map(proposta -> {
                    try {
                        proposta.aprovar();
                        Proposta propostaSalva = repositoryPort.salvar(proposta);
                        log.info("Proposta ID: {} aprovada. Publicando evento...", id);
                        eventPublisherPort.publicarPropostaAprovada(propostaSalva);
                        return Result.success(propostaSalva);
                    } catch (IllegalStateException e) {
                        log.error("Erro ao aprovar proposta ID: {}. Motivo: {}", id, e.getMessage());
                        return Result.<Proposta>failure(e.getMessage());
                    }
                })
                .orElseGet(() -> {
                    log.warn("Tentativa de aprovação de proposta inexistente. ID: {}", id);
                    return Result.failure("Proposta não encontrada com ID: " + id);
                });
    }

    @Override
    @Transactional
    public Result<Proposta> contratar(Long id) {
        log.info("Efetivando contratação da proposta ID: {}", id);
        return repositoryPort.buscarPorId(id)
                .map(proposta -> {
                    try {
                        proposta.contratar();
                        Proposta propostaSalva = repositoryPort.salvar(proposta);
                        log.info("Proposta ID: {} contratada com sucesso.", id);
                        return Result.success(propostaSalva);
                    } catch (IllegalStateException e) {
                        log.error("Erro ao contratar proposta ID: {}. Motivo: {}", id, e.getMessage());
                        return Result.<Proposta>failure(e.getMessage());
                    }
                })
                .orElseGet(() -> {
                    log.warn("Tentativa de contratação de proposta inexistente. ID: {}", id);
                    return Result.failure("Proposta não encontrada com ID: " + id);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Result<Proposta> buscarPorId(Long id) {
        log.info("Buscando proposta por ID: {}", id);
        return repositoryPort.buscarPorId(id)
                .map(Result::success)
                .orElseGet(() -> {
                    log.warn("Proposta não encontrada com ID: {}", id);
                    return Result.failure("Proposta não encontrada com ID: " + id);
                });
    }
}
