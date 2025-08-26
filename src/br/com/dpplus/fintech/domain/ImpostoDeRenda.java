package br.com.dpplus.fintech.domain;

import br.com.dpplus.fintech.util.IdGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ImpostoDeRenda {
    private final long idIr = IdGenerator.nextId();
    private final long idUsuario;
    private final int ano;
    private final BigDecimal valorTotalApurado;
    private final String statusCalculo;
    private final LocalDateTime dataGeracao = LocalDateTime.now();
    private final String observacoes;

    public ImpostoDeRenda(long idUsuario, int ano, BigDecimal valorTotalApurado,
                          String statusCalculo, String observacoes) {
        this.idUsuario = idUsuario; this.ano = ano; this.valorTotalApurado = valorTotalApurado;
        this.statusCalculo = statusCalculo; this.observacoes = observacoes;
    }
    public long getIdIr() { return idIr; }
    public long getIdUsuario() { return idUsuario; }
    public int getAno() { return ano; }
    public BigDecimal getValorTotalApurado() { return valorTotalApurado; }
    public String getStatusCalculo() { return statusCalculo; }
    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public String getObservacoes() { return observacoes; }
}