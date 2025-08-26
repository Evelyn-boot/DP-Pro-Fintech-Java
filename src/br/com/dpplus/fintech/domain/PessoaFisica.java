package br.com.dpplus.fintech.domain;

public class PessoaFisica extends Pessoa {
    private String nome;
    private String cpf;

    public PessoaFisica(long idUsuario, String nome, String cpf) {
        super(idUsuario);
        this.nome = nome;
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}