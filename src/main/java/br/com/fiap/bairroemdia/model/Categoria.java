package br.com.fiap.bairroemdia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "TB_CATEGORIA")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_categoria")
    @SequenceGenerator(name = "seq_categoria", sequenceName = "SEQ_CATEGORIA", allocationSize = 1)
    @Column(name = "ID_CATEGORIA")
    private Long id;

    @Column(name = "NOME", nullable = false, unique = true, length = 80)
    private String nome;

    @Column(name = "DESCRICAO", length = 255)
    private String descricao;

    /** Construtor vazio exigido pelo JPA. */
    protected Categoria() {
    }

    public Categoria(String nome, String descricao) {
        atualizar(nome, descricao);
    }

    public void atualizar(String nome, String descricao) {
        this.nome = Validacao.texto(nome, "nome", 80);
        this.descricao = Validacao.textoOpcional(descricao, "descricao", 255);
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }
}
