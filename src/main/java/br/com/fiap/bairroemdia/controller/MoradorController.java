package br.com.fiap.bairroemdia.controller;

import br.com.fiap.bairroemdia.bo.MoradorBo;
import br.com.fiap.bairroemdia.dto.MoradorRequest;
import br.com.fiap.bairroemdia.dto.MoradorResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/moradores")
public class MoradorController {

    private final MoradorBo bo;

    public MoradorController(MoradorBo bo) {
        this.bo = bo;
    }

    @GetMapping
    public List<MoradorResponse> listar() {
        return bo.listar().stream().map(MoradorResponse::from).toList();
    }

    @GetMapping("/{id}")
    public MoradorResponse buscar(@PathVariable Long id) {
        return MoradorResponse.from(bo.buscar(id));
    }

    @PostMapping
    public ResponseEntity<MoradorResponse> criar(@Valid @RequestBody MoradorRequest req) {
        MoradorResponse criado = MoradorResponse.from(bo.criar(req));
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(local).body(criado);
    }

    @PutMapping("/{id}")
    public MoradorResponse atualizar(@PathVariable Long id, @Valid @RequestBody MoradorRequest req) {
        return MoradorResponse.from(bo.atualizar(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
