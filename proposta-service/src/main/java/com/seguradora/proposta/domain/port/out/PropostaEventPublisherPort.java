package com.seguradora.proposta.domain.port.out;

import com.seguradora.proposta.domain.model.Proposta;

public interface PropostaEventPublisherPort {
    void publicarPropostaAprovada(Proposta proposta);
}
