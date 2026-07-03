package com.seguradora.proposta.domain.port.in;

import com.seguradora.proposta.domain.model.Proposta;
import com.seguradora.proposta.domain.model.Result;

public interface BuscarPropostaUseCase {
    Result<Proposta> buscarPorId(Long id);
}
