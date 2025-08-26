package br.com.dpplus.fintech.domain;

import br.com.dpplus.fintech.domain.enums.TipoSugestao;
import br.com.dpplus.fintech.util.IdGenerator;

import java.time.LocalDateTime;

public class InteligenciaArtificial {
    private final long idIa = IdGenerator.nextId();
    private final long idUsuario;
    private final TipoSugestao tipoSugestao;
    private final String descricao;
    private final LocalDateTime dataGeracao = LocalDateTime.now();

    public InteligenciaArtificial(long idUsuario, TipoSugestao tipoSugestao, String descricao) {
        this.idUsuario = idUsuario; this.tipoSugestao = tipoSugestao; this.descricao = descricao;
    }
    public long getIdIa() { return idIa; }
    public long getIdUsuario() { return idUsuario; }
    public TipoSugestao getTipoSugestao() { return tipoSugestao; }
    public String getDescricao() { return descricao; }
    public LocalDateTime getDataGeracao() { return dataGeracao; }

    @Override public String toString() { return tipoSugestao + ": " + descricao; }
}