package com.seguradora.contratacao.adapter.out.persistence;

import com.seguradora.contratacao.adapter.out.persistence.jpa.ContratacaoEntity;
import com.seguradora.contratacao.adapter.out.persistence.jpa.SpringDataContratacaoRepository;
import com.seguradora.contratacao.domain.model.Contratacao;
import com.seguradora.contratacao.domain.port.out.ContratacaoRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContratacaoRepositoryAdapter implements ContratacaoRepositoryPort {

    private final SpringDataContratacaoRepository springDataRepository;

    @Override
    public Contratacao salvar(Contratacao contratacao) {
        ContratacaoEntity entity = toEntity(contratacao);
        ContratacaoEntity savedEntity = springDataRepository.save(entity);
        return toDomain(savedEntity);
    }

    private ContratacaoEntity toEntity(Contratacao domain) {
        if (domain == null) {
            return null;
        }
        return ContratacaoEntity.builder()
                .id(domain.getId())
                .propostaId(domain.getPropostaId())
                .dataContratacao(domain.getDataContratacao())
                .deletado(false)
                .build();
    }

    private Contratacao toDomain(ContratacaoEntity entity) {
        if (entity == null) {
            return null;
        }
        return Contratacao.builder()
                .id(entity.getId())
                .propostaId(entity.getPropostaId())
                .dataContratacao(entity.getDataContratacao())
                .build();
    }
}
