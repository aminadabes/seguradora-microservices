package com.seguradora.proposta.adapter.in.rest.dto;

import com.seguradora.proposta.domain.model.Proposta;
import com.seguradora.proposta.domain.model.PropostaStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PropostaResponse {
    private Long id;
    private Double valor;
    private String clienteNome;
    private String clienteCpf;
    private PropostaStatus status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public static PropostaResponse fromDomain(Proposta domain) {
        if (domain == null) {
            return null;
        }
        return PropostaResponse.builder()
                .id(domain.getId())
                .valor(domain.getValor())
                .clienteNome(domain.getClienteNome())
                .clienteCpf(domain.getClienteCpf())
                .status(domain.getStatus())
                .dataCriacao(domain.getDataCriacao())
                .dataAtualizacao(domain.getDataAtualizacao())
                .build();
    }
}
