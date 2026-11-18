package br.com.fiap.bairroemdia.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/** Rota raiz: confirma que a API está no ar (usada como health check no Render). Não acessa o banco. */
@RestController
public class StatusController {

    @GetMapping("/")
    public Map<String, Object> status() {
        return Map.of(
                "aplicacao", "Bairro em Dia - CitizenTech",
                "status", "online",
                "recursos", List.of("/moradores", "/categorias", "/ocorrencias"));
    }
}
