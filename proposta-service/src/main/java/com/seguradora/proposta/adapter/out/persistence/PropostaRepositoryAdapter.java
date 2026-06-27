package com.seguradora.proposta.adapter.out.persistence;

import com.seguradora.proposta.adapter.out.persistence.jpa.PropostaEntity;
import com.seguradora.proposta.adapter.out.persistence.jpa.SpringDataPropostaRepository;
import com.seguradora.proposta.domain.model.Proposta;
import com.seguradora.proposta.domain.port.out.PropostaRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PropostaRepositoryAdapter implements PropostaRepositoryPort {

    private final SpringDataPropostaRepository springDataRepository;

    @Override
    public Proposta salvar(Proposta proposta) {
        PropostaEntity entity = toEntity(proposta);
        PropostaEntity savedEntity = springDataRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Proposta> buscarPorId(Long id) {
        return springDataRepository.findById(id)
                .map(this::toDomain);
    }

    private PropostaEntity toEntity(Proposta domain) {
        if (domain == null) {
            return null;
        }
        return PropostaEntity.builder()
                .id(domain.getId())
                .valor(domain.getValor())
                .clienteNome(domain.getClienteNome())
                .clienteCpf(domain.getClienteCpf())
                .status(domain.getStatus())
                .dataCriacao(domain.getDataCriacao())
                .dataAtualizacao(domain.getDataAtualizacao())
                .deletado(false) // default when saving active entity
                .build();
    }

    private Proposta toDomain(PropostaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Proposta.builder()
                .id(entity.getId())
                .valor(entity.getValor())
                .clienteNome(entity.getClienteNome())
                .clienteCpf(entity.getClienteCpf())
                .status(entity.getStatus())
                .dataCriacao(entity.getDataCriacao())
                .dataAtualizacao(entity.getDataAtualizacao())
                .build();
    }
}
