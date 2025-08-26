package br.com.dpplus.fintech.service;

import br.com.dpplus.fintech.domain.Despesa;
import br.com.dpplus.fintech.domain.Receita;
import br.com.dpplus.fintech.domain.Subcategoria;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LancamentoService {
    private final List<Despesa> despesas = new ArrayList<>();
    private final List<Receita> receitas = new ArrayList<>();

    public Despesa registrarDespesa(Despesa d, Subcategoria... subcats) {
        if (subcats != null) for (var s : subcats) d.adicionarSubcategoria(s);
        despesas.add(d);
        return d;
    }

    public Receita registrarReceita(Receita r, Subcategoria... subcats) {
        if (subcats != null) for (var s : subcats) r.adicionarSubcategoria(s);
        receitas.add(r);
        return r;
    }

    public List<Despesa> despesasDoUsuario(long idUsuario) {
        return despesas.stream().filter(d -> d.getIdUsuario() == idUsuario).collect(Collectors.toList());
    }

    public List<Receita> receitasDoUsuario(long idUsuario) {
        return receitas.stream().filter(r -> r.getIdUsuario() == idUsuario).collect(Collectors.toList());
    }

    // compatibilidade (se chamar diretamente no service)
    public void adicionarSubcategoria(Despesa d, Subcategoria sc) { if (d != null) d.adicionarSubcategoria(sc); }
    public void adicionarSubcategoria(Receita r, Subcategoria sc) { if (r != null) r.adicionarSubcategoria(sc); }
}