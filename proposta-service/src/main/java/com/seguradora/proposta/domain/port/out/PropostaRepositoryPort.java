package com.seguradora.proposta.domain.port.out;

import com.seguradora.proposta.domain.model.Proposta;

import java.util.Optional;

public interface PropostaRepositoryPort {
    Proposta salvar(Proposta proposta);
    Optional<Proposta> buscarPorId(Long id);
}
