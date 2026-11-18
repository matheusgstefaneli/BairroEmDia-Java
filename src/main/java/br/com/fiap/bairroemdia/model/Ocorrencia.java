package br.com.fiap.bairroemdia.model;

import br.com.fiap.bairroemdia.exception.DadosInvalidosException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_OCORRENCIA")
public class Ocorrencia {

    public static final int PRIORIDADE_MIN = 1;
    public static final int PRIORIDADE_MAX = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_ocorrencia")
    @SequenceGenerator(name = "seq_ocorrencia", sequenceName = "SEQ_OCORRENCIA", allocationSize = 1)
    @Column(name = "ID_OCORRENCIA")
    private Long id;

    @Column(name = "TITULO", nullable = false, length = 120)
    private String titulo;

    @Column(name = "DESCRICAO", nullable = false, length = 1000)
    private String descricao;

    @Column(name = "LOCALIZACAO", nullable = false, length = 255)
    private String localizacao;

    @Column(name = "PRIORIDADE", nullable = false)
    private Integer prioridade;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS_OCORRENCIA", nullable = false, length = 20)
    private StatusOcorrencia status;

    @Column(name = "DATA_CRIACAO", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_MORADOR", nullable = false, updatable = false)
    private Morador morador;

    @ManyToOne(optional = false)
    @JoinColumn(name = "ID_CATEGORIA", nullable = false)
    private Categoria categoria;

    /** Construtor vazio exigido pelo JPA. */
    protected Ocorrencia() {
    }

    /** Toda ocorrência nasce ABERTA, com data de criação definida pelo sistema. */
    public Ocorrencia(String titulo, String descricao, String localizacao, Integer prioridade,
                      Morador morador, Categoria categoria) {
        if (morador == null) {
            throw new DadosInvalidosException("A ocorrência deve estar vinculada a um morador.");
        }
        atualizarDados(titulo, descricao, localizacao, categoria);
        alterarPrioridade(prioridade);
        this.morador = morador;
        this.status = StatusOcorrencia.ABERTA;
        this.dataCriacao = LocalDateTime.now();
    }

    public void atualizarDados(String titulo, String descricao, String localizacao, Categoria categoria) {
        if (categoria == null) {
            throw new DadosInvalidosException("A ocorrência deve estar vinculada a uma categoria.");
        }
        this.titulo = Validacao.texto(titulo, "titulo", 120);
        this.descricao = Validacao.texto(descricao, "descricao", 1000);
        this.localizacao = Validacao.texto(localizacao, "localizacao", 255);
        this.categoria = categoria;
    }

    public void alterarPrioridade(Integer prioridade) {
        if (prioridade == null || prioridade < PRIORIDADE_MIN || prioridade > PRIORIDADE_MAX) {
            throw new DadosInvalidosException("A prioridade deve estar entre " + PRIORIDADE_MIN + " e " + PRIORIDADE_MAX + ".");
        }
        this.prioridade = prioridade;
    }

    /** A validação da transição é regra de negócio e fica na BO. */
    public void alterarStatus(StatusOcorrencia novoStatus) {
        if (novoStatus == null) {
            throw new DadosInvalidosException("O status é obrigatório.");
        }
        this.status = novoStatus;
    }

    public boolean isFinalizada() {
        return status != null && status.isFinal();
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public Integer getPrioridade() {
        return prioridade;
    }

    public StatusOcorrencia getStatus() {
        return status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public Morador getMorador() {
        return morador;
    }

    public Categoria getCategoria() {
        return categoria;
    }
}
