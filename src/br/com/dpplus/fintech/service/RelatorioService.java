package br.com.dpplus.fintech.service;

import br.com.dpplus.fintech.domain.Categoria;
import br.com.dpplus.fintech.domain.Despesa;
import br.com.dpplus.fintech.domain.Receita;
import br.com.dpplus.fintech.domain.Subcategoria;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class RelatorioService {

    public Map<Categoria, BigDecimal> totalPorCategoria(List<? extends Object> lancs, YearMonth mes) {
        Map<Categoria, BigDecimal> mapa = new LinkedHashMap<>();
        for (Object o : lancs) {
            if (o instanceof Despesa d) {
                if (d.getDataVencimento() != null
                        && YearMonth.from(d.getDataVencimento()).equals(mes)
                        && !d.isPendente()
                        && d.getCategoria() != null) {
                    mapa.merge(d.getCategoria(), d.getValor(), BigDecimal::add);
                }
            }
            if (o instanceof Receita r) {
                if (r.getDataEntrada() != null
                        && YearMonth.from(r.getDataEntrada()).equals(mes)
                        && !r.isPendente()
                        && r.getCategoria() != null) {
                    mapa.merge(r.getCategoria(), r.getValor(), BigDecimal::add);
                }
            }
        }
        return mapa;
    }

    public Map<Subcategoria, BigDecimal> totalPorSubcategoria(List<? extends Object> lancs, YearMonth mes) {
        Map<Subcategoria, BigDecimal> mapa = new LinkedHashMap<>();
        for (Object o : lancs) {
            if (o instanceof Despesa d) {
                if (d.getDataVencimento() != null
                        && YearMonth.from(d.getDataVencimento()).equals(mes)
                        && !d.isPendente()
                        && d.getSubcategoria() != null) {
                    mapa.merge(d.getSubcategoria(), d.getValor(), BigDecimal::add);
                }
            }
            if (o instanceof Receita r) {
                if (r.getDataEntrada() != null
                        && YearMonth.from(r.getDataEntrada()).equals(mes)
                        && !r.isPendente()
                        && r.getSubcategoria() != null) {
                    mapa.merge(r.getSubcategoria(), r.getValor(), BigDecimal::add);
                }
            }
        }
        return mapa;
    }

    // ---- IA simples: sugestões de investimento/otimização para o mês selecionado ----
    public List<String> sugestoesInvestimento(List<Receita> receitas, List<Despesa> despesas, YearMonth mes) {
        var msgs = new ArrayList<String>();

        BigDecimal totalR = receitas.stream()
                .filter(r -> r.getDataEntrada() != null && YearMonth.from(r.getDataEntrada()).equals(mes) && !r.isPendente())
                .map(Receita::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalD = despesas.stream()
                .filter(d -> d.getDataVencimento() != null && YearMonth.from(d.getDataVencimento()).equals(mes) && !d.isPendente())
                .map(Despesa::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldo = totalR.subtract(totalD);

        if (saldo.compareTo(BigDecimal.ZERO) > 0) {
            msgs.add("Você tem superávit de " + saldo + ". Considere investir 60% em Tesouro Selic, 30% em CDI/LCI e 10% em ações/ETFs.");
        } else if (saldo.compareTo(BigDecimal.ZERO) < 0) {
            msgs.add("Seu mês está no negativo (" + saldo + "). Reduza gastos e evite novas dívidas; use relatórios por categoria para priorizar cortes.");
        } else {
            msgs.add("Saldo zerado. Reserve parte de receitas futuras para construir uma reserva de emergência (ideal: 6 meses de despesas).");
        }

        // Top categorias de despesa (para cortar)
        Map<Categoria, BigDecimal> porCat = despesas.stream()
                .filter(d -> d.getDataVencimento() != null && YearMonth.from(d.getDataVencimento()).equals(mes) && !d.isPendente() && d.getCategoria() != null)
                .collect(Collectors.groupingBy(Despesa::getCategoria,
                        LinkedHashMap::new,
                        Collectors.mapping(Despesa::getValor, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        porCat.entrySet().stream()
                .sorted(Map.Entry.<Categoria, BigDecimal>comparingByValue().reversed())
                .limit(2)
                .forEach(e -> msgs.add("Gasto alto em '" + e.getKey().getNome() + "' (" + e.getValue() + "). Veja oportunidades de economia."));

        return msgs;
    }
}