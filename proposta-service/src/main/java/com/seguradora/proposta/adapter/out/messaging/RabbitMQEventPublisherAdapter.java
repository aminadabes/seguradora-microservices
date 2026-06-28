package com.seguradora.proposta.adapter.out.messaging;

import com.seguradora.proposta.config.RabbitMQConfig;
import com.seguradora.proposta.domain.model.Proposta;
import com.seguradora.proposta.domain.port.out.PropostaEventPublisherPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQEventPublisherAdapter implements PropostaEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publicarPropostaAprovada(Proposta proposta) {
        String correlationId = MDC.get("correlationId");
        log.info("Publicando evento de proposta aprovada no RabbitMQ. Proposta ID: {}", proposta.getId());

        PropostaAprovadaEvent event = new PropostaAprovadaEvent(proposta.getId());

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PROPOSAL_EXCHANGE,
                RabbitMQConfig.PROPOSAL_APPROVED_ROUTING_KEY,
                event,
                message -> {
                    if (correlationId != null) {
                        message.getMessageProperties().setCorrelationId(correlationId);
                        message.getMessageProperties().setHeader("X-Correlation-ID", correlationId);
                    }
                    return message;
                }
        );
    }
}
