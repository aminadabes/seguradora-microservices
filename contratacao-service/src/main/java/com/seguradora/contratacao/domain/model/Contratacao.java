package com.seguradora.contratacao.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contratacao {
    private Long id;
    private Long propostaId;
    private LocalDateTime dataContratacao;
}
