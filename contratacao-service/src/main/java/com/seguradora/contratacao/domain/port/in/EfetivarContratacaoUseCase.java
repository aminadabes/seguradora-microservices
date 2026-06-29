package com.seguradora.contratacao.domain.port.in;

import com.seguradora.contratacao.domain.model.Contratacao;
import com.seguradora.contratacao.domain.model.Result;

public interface EfetivarContratacaoUseCase {
    Result<Contratacao> efetivar(Long propostaId);
}
