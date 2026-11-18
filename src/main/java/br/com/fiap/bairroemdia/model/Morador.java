package br.com.fiap.bairroemdia.model;

import br.com.fiap.bairroemdia.exception.DadosInvalidosException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.util.regex.Pattern;

@Entity
@Table(name = "TB_MORADOR")
public class Morador {

    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_morador")
    @SequenceGenerator(name = "seq_morador", sequenceName = "SEQ_MORADOR", allocationSize = 1)
    @Column(name = "ID_MORADOR")
    private Long id;

    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 150)
    private String email;

    /** Construtor vazio exigido pelo JPA. */
    protected Morador() {
    }

    public Morador(String nome, String email) {
        atualizar(nome, email);
    }

    public void atualizar(String nome, String email) {
        this.nome = Validacao.texto(nome, "nome", 100);
        String e = Validacao.texto(email, "email", 150).toLowerCase();
        if (!EMAIL.matcher(e).matches()) {
            throw new DadosInvalidosException("O campo 'email' possui formato inválido.");
        }
        this.email = e;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }
}
