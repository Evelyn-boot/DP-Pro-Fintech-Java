package br.com.dpplus.fintech.domain;

import br.com.dpplus.fintech.util.IdGenerator;

import java.time.LocalDateTime;

public class Notificacao {
    private final long idNotificacao = IdGenerator.nextId();
    private final long idUsuario;
    private final String tipo;
    private final String mensagem;
    private final LocalDateTime dataEnvio = LocalDateTime.now();
    private boolean foiLida;

    public Notificacao(long idUsuario, String tipo, String mensagem) {
        this.idUsuario = idUsuario; this.tipo = tipo; this.mensagem = mensagem;
    }
    public long getIdNotificacao() { return idNotificacao; }
    public long getIdUsuario() { return idUsuario; }
    public String getTipo() { return tipo; }
    public String getMensagem() { return mensagem; }
    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public boolean isFoiLida() { return foiLida; }
    public void marcarLida() { this.foiLida = true; }
}