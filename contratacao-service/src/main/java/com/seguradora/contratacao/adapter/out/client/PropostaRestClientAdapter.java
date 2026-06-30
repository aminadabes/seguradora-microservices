package com.seguradora.contratacao.adapter.out.client;

import com.seguradora.contratacao.domain.model.Result;
import com.seguradora.contratacao.domain.port.out.PropostaClientPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class PropostaRestClientAdapter implements PropostaClientPort {

    private final RestClient proposalRestClient;

    @Override
    public Result<Void> marcarComoContratada(Long propostaId) {
        log.info("Efetuando requisição REST HTTP PUT para marcar proposta ID {} como CONTRATADA", propostaId);

        try {
            proposalRestClient.put()
                    .uri("/api/propostas/{id}/contratar", propostaId)
                    .retrieve()
                    .toBodilessEntity();
            
            log.info("Proposta ID {} marcada com sucesso como CONTRATADA via REST.", propostaId);
            return Result.success(null);
        } catch (Exception e) {
            log.error("Falha ao marcar proposta ID {} como CONTRATADA no PropostaService: {}", propostaId, e.getMessage());
            return Result.failure("Falha na integração: " + e.getMessage());
        }
    }
}
