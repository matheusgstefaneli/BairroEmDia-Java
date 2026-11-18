package br.com.fiap.bairroemdia.controller;

import br.com.fiap.bairroemdia.bo.CategoriaBo;
import br.com.fiap.bairroemdia.dto.CategoriaRequest;
import br.com.fiap.bairroemdia.dto.CategoriaResponse;
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
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaBo bo;

    public CategoriaController(CategoriaBo bo) {
        this.bo = bo;
    }

    @GetMapping
    public List<CategoriaResponse> listar() {
        return bo.listar().stream().map(CategoriaResponse::from).toList();
    }

    @GetMapping("/{id}")
    public CategoriaResponse buscar(@PathVariable Long id) {
        return CategoriaResponse.from(bo.buscar(id));
    }

    @PostMapping
    public ResponseEntity<CategoriaResponse> criar(@Valid @RequestBody CategoriaRequest req) {
        CategoriaResponse criada = CategoriaResponse.from(bo.criar(req));
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(local).body(criada);
    }

    @PutMapping("/{id}")
    public CategoriaResponse atualizar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest req) {
        return CategoriaResponse.from(bo.atualizar(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        bo.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
