package com.seguradora.contratacao.domain.port.out;

import com.seguradora.contratacao.domain.model.Contratacao;

public interface ContratacaoRepositoryPort {
    Contratacao salvar(Contratacao contratacao);
}
