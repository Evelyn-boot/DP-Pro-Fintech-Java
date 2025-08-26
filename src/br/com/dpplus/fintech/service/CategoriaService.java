package br.com.dpplus.fintech.service;

import br.com.dpplus.fintech.domain.Categoria;
import br.com.dpplus.fintech.domain.Subcategoria;
import br.com.dpplus.fintech.domain.enums.TipoCategoria;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CategoriaService {
    private final List<Categoria> categorias = new ArrayList<>();
    private final List<Subcategoria> subcategorias = new ArrayList<>();

    // CRIAR
    public Categoria criarCategoria(String nome, TipoCategoria tipo) {
        var c = new Categoria(nome, tipo);
        categorias.add(c);
        return c;
    }
    public Subcategoria criarSubcategoria(String nome, Categoria categoria) {
        var sc = new Subcategoria(nome, categoria);
        subcategorias.add(sc);
        return sc;
    }

    // EDITAR (apenas nome – mantém o id, então lançamentos antigos continuam válidos)
    public void editarCategoria(Categoria c, String novoNome) {
        if (c == null) return;
        categorias.removeIf(x -> x.getId() == c.getId());
        categorias.add(new Categoria(novoNome, c.getTipo())); // simples – se quiser manter o mesmo objeto, pode trocar por reflection ou criar setters
    }
    public void editarSubcategoria(Subcategoria sc, String novoNome) {
        if (sc == null) return;
        subcategorias.removeIf(x -> x.getId() == sc.getId());
        subcategorias.add(new Subcategoria(novoNome, sc.getCategoria()));
    }

    // EXCLUIR (não remove lançamentos; decisão é do domínio do lançamento)
    public void excluirCategoria(Categoria c) {
        if (c == null) return;
        subcategorias.removeIf(sc -> sc.getCategoria().getId() == c.getId());
        categorias.removeIf(x -> x.getId() == c.getId());
    }
    public void excluirSubcategoria(Subcategoria sc) {
        if (sc == null) return;
        subcategorias.removeIf(x -> x.getId() == sc.getId());
    }

    // LISTAS
    public List<Categoria> listarCategorias(TipoCategoria tipo) {
        return categorias.stream()
                .filter(c -> c.getTipo() == tipo)
                .collect(Collectors.toList());
    }

    public List<Categoria> listarTodasCategorias() {
        return new ArrayList<>(categorias);
    }

    public List<Subcategoria> listarSubcategorias(Categoria categoria) {
        return subcategorias.stream()
                .filter(sc -> sc.getCategoria().getId() == categoria.getId())
                .collect(Collectors.toList());
    }
}
