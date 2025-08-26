package br.com.dpplus.fintech.domain;

import br.com.dpplus.fintech.domain.enums.TipoCategoria;
import br.com.dpplus.fintech.util.IdGenerator;

public class Categoria {
    private final long id = IdGenerator.nextId();
    private final String nome;
    private final TipoCategoria tipo;

    public Categoria(String nome, TipoCategoria tipo) {
        this.nome = nome;
        this.tipo = tipo;
    }

    public long getId() { return id; }
    public String getNome() { return nome; }
    public TipoCategoria getTipo() { return tipo; }

    @Override
    public String toString() { return nome + " (" + tipo + ")"; }
}