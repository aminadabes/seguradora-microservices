package com.seguradora.proposta.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String PROPOSAL_EXCHANGE = "proposta.exchange";
    public static final String PROPOSAL_APPROVED_QUEUE = "proposta.aprovada.queue";
    public static final String PROPOSAL_APPROVED_ROUTING_KEY = "proposta.aprovada";

    @Bean
    public TopicExchange proposalExchange() {
        return new TopicExchange(PROPOSAL_EXCHANGE);
    }

    @Bean
    public Queue proposalApprovedQueue() {
        return new Queue(PROPOSAL_APPROVED_QUEUE, true);
    }

    @Bean
    public Binding bindingProposalApproved(Queue proposalApprovedQueue, TopicExchange proposalExchange) {
        return BindingBuilder.bind(proposalApprovedQueue).to(proposalExchange).with(PROPOSAL_APPROVED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
