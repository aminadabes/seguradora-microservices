package com.seguradora.contratacao.domain.port.out;

import com.seguradora.contratacao.domain.model.Result;

public interface PropostaClientPort {
    Result<Void> marcarComoContratada(Long propostaId);
}
