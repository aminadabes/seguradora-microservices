package com.seguradora.proposta.adapter.out.persistence.jpa;

import com.seguradora.proposta.domain.model.PropostaStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_proposta")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE tb_proposta SET deletado = true, data_exclusao = NOW() WHERE id = ?")
@Where(clause = "deletado = false")
public class PropostaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "valor", nullable = false)
    private Double valor;

    @Column(name = "cliente_nome", nullable = false, length = 150)
    private String clienteNome;

    @Column(name = "cliente_cpf", nullable = false, length = 14)
    private String clienteCpf;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PropostaStatus status;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @Column(name = "deletado", nullable = false)
    private Boolean deletado = false;

    @Column(name = "data_exclusao")
    private LocalDateTime dataExclusao;
}
