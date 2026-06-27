package com.seguradora.proposta.adapter.out.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataPropostaRepository extends JpaRepository<PropostaEntity, Long> {
}
