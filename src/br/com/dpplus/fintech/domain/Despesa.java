package br.com.dpplus.fintech.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Despesa {
    private final long idUsuario;
    private final String descricao;
    private final BigDecimal valor;
    private final LocalDate dataVencimento;
    private final boolean recorrente;
    private final boolean pendente;

    private Categoria categoria;
    private Subcategoria subcategoria;

    // Construtor (sem preenchimento automático)
    public Despesa(long idUsuario, String descricao, BigDecimal valor,
                   LocalDate dataVencimento, boolean recorrente, boolean pendente) {
        this.idUsuario = idUsuario;
        this.descricao = descricao;
        this.valor = valor;
        this.dataVencimento = dataVencimento;
        this.recorrente = recorrente;
        this.pendente = pendente;
    }

    // Classificação (exclusiva)
    public void setCategoria(Categoria categoria) {
        if (categoria == null) throw new IllegalArgumentException("Categoria não pode ser nula.");
        this.categoria = categoria;
        this.subcategoria = null;
    }
    public void setSubcategoria(Subcategoria subcategoria) {
        if (subcategoria == null) throw new IllegalArgumentException("Subcategoria não pode ser nula.");
        this.subcategoria = subcategoria;
        this.categoria = null;
    }
    public void adicionarSubcategoria(Subcategoria sc) { setSubcategoria(sc); }

    // Getters
    public long getIdUsuario() { return idUsuario; }
    public String getDescricao() { return descricao; }
    public BigDecimal getValor() { return valor; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public boolean isRecorrente() { return recorrente; }
    public boolean isPendente() { return pendente; }
    public Categoria getCategoria() { return categoria; }
    public Subcategoria getSubcategoria() { return subcategoria; }
}