package com.seguradora.proposta.adapter.in.rest;

import com.seguradora.proposta.adapter.in.rest.dto.ApiResponse;
import com.seguradora.proposta.adapter.in.rest.dto.PropostaRequest;
import com.seguradora.proposta.adapter.in.rest.dto.PropostaResponse;
import com.seguradora.proposta.domain.model.Proposta;
import com.seguradora.proposta.domain.model.Result;
import com.seguradora.proposta.domain.port.in.AtualizarStatusPropostaUseCase;
import com.seguradora.proposta.domain.port.in.ContratarPropostaUseCase;
import com.seguradora.proposta.domain.port.in.CriarPropostaUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/propostas")
@RequiredArgsConstructor
public class PropostaController {

    private final CriarPropostaUseCase criarPropostaUseCase;
    private final AtualizarStatusPropostaUseCase atualizarStatusPropostaUseCase;
    private final ContratarPropostaUseCase contratarPropostaUseCase;

    @PostMapping
    public ResponseEntity<ApiResponse<PropostaResponse>> criar(@Valid @RequestBody PropostaRequest request) {
        Proposta proposta = Proposta.builder()
                .valor(request.getValor())
                .clienteNome(request.getClienteNome())
                .clienteCpf(request.getClienteCpf())
                .build();

        Result<Proposta> result = criarPropostaUseCase.criar(proposta);
        if (result.isFailure()) {
            return ResponseEntity.badRequest().body(ApiResponse.failure(result.getError()));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(PropostaResponse.fromDomain(result.getValue())));
    }

    @PutMapping("/{id}/aprovar")
    public ResponseEntity<ApiResponse<PropostaResponse>> aprovar(@PathVariable Long id) {
        Result<Proposta> result = atualizarStatusPropostaUseCase.aprovar(id);
        if (result.isFailure()) {
            if (result.getError().contains("não encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.failure(result.getError()));
            }
            return ResponseEntity.badRequest().body(ApiResponse.failure(result.getError()));
        }

        return ResponseEntity.ok(ApiResponse.success(PropostaResponse.fromDomain(result.getValue())));
    }

    @PutMapping("/{id}/contratar")
    public ResponseEntity<ApiResponse<PropostaResponse>> contratar(@PathVariable Long id) {
        Result<Proposta> result = contratarPropostaUseCase.contratar(id);
        if (result.isFailure()) {
            if (result.getError().contains("não encontrada")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.failure(result.getError()));
            }
            return ResponseEntity.badRequest().body(ApiResponse.failure(result.getError()));
        }

        return ResponseEntity.ok(ApiResponse.success(PropostaResponse.fromDomain(result.getValue())));
    }
}
