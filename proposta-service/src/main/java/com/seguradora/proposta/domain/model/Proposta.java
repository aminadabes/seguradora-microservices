package com.seguradora.proposta.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Proposta {
    private Long id;
    private Double valor;
    private String clienteNome;
    private String clienteCpf;
    private PropostaStatus status;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public void aprovar() {
        if (this.status != PropostaStatus.EM_ANALISE) {
            throw new IllegalStateException("Apenas propostas EM ANALISE podem ser aprovadas.");
        }
        this.status = PropostaStatus.APROVADA;
        this.dataAtualizacao = LocalDateTime.now();
    }

    public void contratar() {
        if (this.status != PropostaStatus.APROVADA) {
            throw new IllegalStateException("Apenas propostas APROVADAS podem ser contratadas.");
        }
        this.status = PropostaStatus.CONTRATADA;
        this.dataAtualizacao = LocalDateTime.now();
    }
}
