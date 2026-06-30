package com.seguradora.contratacao.adapter.in.messaging;

import com.seguradora.contratacao.config.RabbitMQConfig;
import com.seguradora.contratacao.domain.model.Contratacao;
import com.seguradora.contratacao.domain.model.Result;
import com.seguradora.contratacao.domain.port.in.EfetivarContratacaoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQProposalApprovedConsumer {

    private final EfetivarContratacaoUseCase efetivarContratacaoUseCase;
    private static final String MDC_CORRELATION_KEY = "correlationId";

    @RabbitListener(queues = RabbitMQConfig.PROPOSAL_APPROVED_QUEUE)
    public void consumirPropostaAprovada(PropostaAprovadaEvent event, 
                                         @Header(name = "X-Correlation-ID", required = false) String correlationId) {
        
        // Se não houver Correlation ID na mensagem, geramos um novo para rastreabilidade local
        if (correlationId == null || correlationId.trim().isEmpty()) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put(MDC_CORRELATION_KEY, correlationId);
        log.info("Mensagem consumida da fila proposta.aprovada.queue. Proposta ID: {}", event.propostaId());

        try {
            Result<Contratacao> result = efetivarContratacaoUseCase.efetivar(event.propostaId());
            if (result.isFailure()) {
                log.error("Erro ao efetivar contratação para proposta ID: {}. Motivo: {}", event.propostaId(), result.getError());
            } else {
                log.info("Contratação efetivada com sucesso para proposta ID: {}", event.propostaId());
            }
        } finally {
            MDC.clear(); // Limpa MDC para evitar vazamento de memória de thread
        }
    }
}
