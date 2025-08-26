package br.com.dpplus.fintech.domain;

public class PessoaJuridica extends Pessoa {
    private String razaoSocial;
    private String cnpj;

    public PessoaJuridica(long idUsuario, String razaoSocial, String cnpj) {
        super(idUsuario);
        this.razaoSocial = razaoSocial;
        this.cnpj = cnpj;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }
}