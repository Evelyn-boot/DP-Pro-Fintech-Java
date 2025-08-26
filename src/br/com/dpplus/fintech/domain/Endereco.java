package br.com.dpplus.fintech.domain;

public class Endereco {
    private final long idUsuario;
    private final String rua, numero, complemento, bairro, cidade, estado, cep;

    public Endereco(long idUsuario, String rua, String numero, String complemento,
                    String bairro, String cidade, String estado, String cep) {
        this.idUsuario = idUsuario;
        this.rua = rua; this.numero = numero; this.complemento = complemento;
        this.bairro = bairro; this.cidade = cidade; this.estado = estado; this.cep = cep;
    }
    public long getIdUsuario() { return idUsuario; }
    public String getRua() { return rua; }
    public String getNumero() { return numero; }
    public String getComplemento() { return complemento; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
    public String getCep() { return cep; }
}