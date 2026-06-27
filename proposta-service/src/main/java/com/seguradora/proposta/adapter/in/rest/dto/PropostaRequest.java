package com.seguradora.proposta.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropostaRequest {

    @NotNull(message = "O valor da proposta é obrigatório")
    @Positive(message = "O valor deve ser maior que zero")
    private Double valor;

    @NotBlank(message = "O nome do cliente é obrigatório")
    @Size(max = 150, message = "O nome deve conter no máximo 150 caracteres")
    private String clienteNome;

    @NotBlank(message = "O CPF do cliente é obrigatório")
    @Size(max = 14, message = "O CPF deve conter no máximo 14 caracteres")
    private String clienteCpf;
}
