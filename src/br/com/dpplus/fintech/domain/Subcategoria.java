package br.com.dpplus.fintech.domain;

import br.com.dpplus.fintech.util.IdGenerator;

public class Subcategoria {
    private final long id = IdGenerator.nextId();
    private final String nome;
    private final Categoria categoria;

    public Subcategoria(String nome, Categoria categoria) {
        this.nome = nome;
        this.categoria = categoria;
    }

    public long getId() { return id; }
    public String getNome() { return nome; }
    public Categoria getCategoria() { return categoria; }

    @Override
    public String toString() { return categoria.getNome() + " > " + nome; }
}