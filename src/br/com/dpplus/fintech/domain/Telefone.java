package br.com.dpplus.fintech.domain;

public class Telefone {
    private final long idUsuario;
    private final String ddi, ddd, numero;

    public Telefone(long idUsuario, String ddi, String ddd, String numero) {
        this.idUsuario = idUsuario; this.ddi = ddi; this.ddd = ddd; this.numero = numero;
    }
    public long getIdUsuario() { return idUsuario; }
    public String getDdi() { return ddi; }
    public String getDdd() { return ddd; }
    public String getNumero() { return numero; }
}
