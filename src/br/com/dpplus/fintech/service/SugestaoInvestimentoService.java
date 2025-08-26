package br.com.dpplus.fintech.service;

import br.com.dpplus.fintech.domain.Categoria;
import br.com.dpplus.fintech.domain.Despesa;
import br.com.dpplus.fintech.domain.Receita;
import br.com.dpplus.fintech.domain.Usuario;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class SugestaoInvestimentoService {

    /**
     * Gera recomendações simples com base no saldo do mês e no perfil de gastos.
     * - Calcula sobras (receita - despesa)
     * - Se sobras > 0, sugere alocação em Reserva/Pos-fixado/Multimercado/Ações
     * - Aponta categorias de despesas “pesadas”
     */
    public List<String> gerarSugestoes(
            Usuario usuario,
            List<Receita> receitas,
            List<Despesa> despesas,
            YearMonth mes,
            Map<Categoria, BigDecimal> totalDespesasPorCategoria
    ) {
        List<String> out = new ArrayList<>();

        BigDecimal totalRec = receitas.stream()
                .filter(r -> r.getIdUsuario() == usuario.getIdUsuario())
                .filter(r -> r.getDataEntrada() != null && YearMonth.from(r.getDataEntrada()).equals(mes))
                .map(Receita::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDesp = despesas.stream()
                .filter(d -> d.getIdUsuario() == usuario.getIdUsuario())
                .filter(d -> d.getDataVencimento() != null && YearMonth.from(d.getDataVencimento()).equals(mes))
                .map(Despesa::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal sobra = totalRec.subtract(totalDesp).setScale(2, RoundingMode.HALF_UP);

        out.add("Saldo do mês (" + mes + "): Receitas=" + fmt(totalRec) + " | Despesas=" + fmt(totalDesp) + " | Sobra=" + fmt(sobra));

        if (sobra.compareTo(BigDecimal.ZERO) <= 0) {
            out.add("Você está sem sobra no mês. Priorize reduzir despesas fixas e buscar aumentar receitas.");
            topCategorias(totalDespesasPorCategoria, 3).forEach(c ->
                    out.add("• Despesa elevada em: " + c.getKey().getNome() + " (" + fmt(c.getValue()) + ")"));
            out.add("Dica: corte 10–15% nas 2 categorias mais altas para buscar superávit.");
            return out;
        }

        // Percentuais sugeridos com base na sobra
        Map<String, Integer> aloc = planoAlocacaoPercentual(sobra);
        out.add("Proposta de alocação da sobra:");
        aloc.forEach((nome, pct) -> out.add("• " + nome + ": " + pct + "% (" + fmt(porcento(sobra, pct)) + ")"));

        // Alertas por peso de categoria
        if (totalDespesasPorCategoria != null && !totalDespesasPorCategoria.isEmpty()) {
            out.add("Observações sobre seus gastos:");
            topCategorias(totalDespesasPorCategoria, 3).forEach(c ->
                    out.add("• " + c.getKey().getNome() + " ocupa peso relevante (" + fmt(c.getValue()) + "). Avalie renegociação/cortes."));
        }

        out.add("Regra prática: mantenha 6 meses de despesas em Reserva de Emergência antes de aumentar risco.");
        return out;
    }

    private static Map<String, Integer> planoAlocacaoPercentual(BigDecimal sobra) {
        // Sugerimos perfis conforme o tamanho da sobra: pequena, média, grande
        int reserva = 40;    // Tesouro Selic / CDB liquidez diária
        int pos = 30;        // Pós-fixado/LCI/LCA
        int multi = 20;      // Multimercado conservador
        int acoes = 10;      // Ações/ETFs

        if (sobra.compareTo(new BigDecimal("2000")) >= 0) {
            reserva = 30; pos = 30; multi = 25; acoes = 15;
        } else if (sobra.compareTo(new BigDecimal("700")) <= 0) {
            reserva = 60; pos = 25; multi = 10; acoes = 5;
        }
        Map<String, Integer> m = new LinkedHashMap<>();
        m.put("Reserva de Emergência (Selic/CDB liquidez diária)", reserva);
        m.put("Pós-fixado (LCI/LCA/CDB)", pos);
        m.put("Multimercado conservador", multi);
        m.put("Renda variável (ETFs/Ações)", acoes);
        return m;
    }

    private static List<Map.Entry<Categoria, BigDecimal>> topCategorias(Map<Categoria, BigDecimal> mapa, int n) {
        return mapa.entrySet().stream()
                .sorted(Map.Entry.<Categoria, BigDecimal>comparingByValue().reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    private static String fmt(BigDecimal v) {
        return "R$ " + v.setScale(2, RoundingMode.HALF_UP).toPlainString().replace(".", ",");
    }
    private static BigDecimal porcento(BigDecimal base, int pct) {
        return base.multiply(new BigDecimal(pct)).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
    }
}