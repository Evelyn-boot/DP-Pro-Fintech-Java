package br.com.dpplus.fintech.domain;

import br.com.dpplus.fintech.domain.enums.TipoUsuario;
import br.com.dpplus.fintech.util.IdGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Usuario {
    private final long idUsuario = IdGenerator.nextId();
    private final TipoUsuario tipoUsuario;     // PF ou PJ
    private final String email;
    private final String senha;
    private final LocalDateTime criadoEm = LocalDateTime.now();

    private PessoaFisica pf;    // um (opcional) - conforme MER
    private PessoaJuridica pj;  // um (opcional)
    private Endereco endereco;  // um (opcional)
    private final List<Telefone> telefones = new ArrayList<>();

    public Usuario(TipoUsuario tipoUsuario, String email, String senha) {
        this.tipoUsuario = tipoUsuario;
        this.email = email;
        this.senha = senha;
    }
    public long getIdUsuario() { return idUsuario; }
    public TipoUsuario getTipoUsuario() { return tipoUsuario; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public LocalDateTime getCriadoEm() { return criadoEm; }

    public PessoaFisica getPf() { return pf; }
    public PessoaJuridica getPj() { return pj; }
    public Endereco getEndereco() { return endereco; }
    public List<Telefone> getTelefones() { return telefones; }

    public void definirPessoaFisica(PessoaFisica pf) { this.pf = pf; }
    public void definirPessoaJuridica(PessoaJuridica pj) { this.pj = pj; }
    public void definirEndereco(Endereco e) { this.endereco = e; }
    public void adicionarTelefone(Telefone t) { this.telefones.add(t); }
}

