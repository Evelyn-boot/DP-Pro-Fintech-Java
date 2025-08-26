package br.com.dpplus.fintech.domain;

public abstract class Pessoa {
    private long idUsuario;

    public Pessoa(long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(long idUsuario) {
        this.idUsuario = idUsuario;
    }
}