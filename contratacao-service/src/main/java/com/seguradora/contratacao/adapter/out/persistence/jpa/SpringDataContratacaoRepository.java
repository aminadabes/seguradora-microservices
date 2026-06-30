package com.seguradora.contratacao.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataContratacaoRepository extends JpaRepository<ContratacaoEntity, Long> {
}
