package br.com.fiap.bairroemdia.controller;

import br.com.fiap.bairroemdia.bo.OcorrenciaBo;
import br.com.fiap.bairroemdia.dto.OcorrenciaRequest;
import br.com.fiap.bairroemdia.dto.OcorrenciaResponse;
import br.com.fiap.bairroemdia.dto.OcorrenciaUpdateRequest;
import br.com.fiap.bairroemdia.dto.PrioridadeRequest;
import br.com.fiap.bairroemdia.dto.StatusRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/ocorrencias")
public class OcorrenciaController {

    private final OcorrenciaBo bo;

    public OcorrenciaController(OcorrenciaBo bo) {
        this.bo = bo;
    }

    @GetMapping
    public List<OcorrenciaResponse> listar() {
        return bo.listar().stream().map(OcorrenciaResponse::from).toList();
    }

    @GetMapping("/{id}")
    public OcorrenciaResponse buscar(@PathVariable Long id) {
        return OcorrenciaResponse.from(bo.buscar(id));
    }

    @PostMapping
    public ResponseEntity<OcorrenciaResponse> criar(@Valid @RequestBody OcorrenciaRequest req) {
        OcorrenciaResponse criada = OcorrenciaResponse.from(bo.criar(req));
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(local).body(criada);
    }

    @PutMapping("/{id}")
    public OcorrenciaResponse atualizar(@PathVariable Long id, @Valid @RequestBody OcorrenciaUpdateRequest req) {
        return OcorrenciaResponse.from(bo.atualizar(id, req));
    }

    /** Atualização parcial: apenas o status (regras de transição na BO). */
    @PatchMapping("/{id}/status")
    public OcorrenciaResponse alterarStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest req) {
        return OcorrenciaResponse.from(bo.alterarStatus(id, req.status()));
    }

    /** Atualização parcial: apenas a prioridade (1 a 5). */
    @PatchMapping("/{id}/prioridade")
    public OcorrenciaResponse alterarPrioridade(@PathVariable Long id, @Valid @RequestBody PrioridadeRequest req) {
        return OcorrenciaResponse.from(bo.alterarPrioridade(id, req.prioridade()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
