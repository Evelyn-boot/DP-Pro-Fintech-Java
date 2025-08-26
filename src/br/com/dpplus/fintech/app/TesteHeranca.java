package br.com.dpplus.fintech.app;

import br.com.dpplus.fintech.domain.*;
import br.com.dpplus.fintech.domain.enums.TipoCategoria;
import br.com.dpplus.fintech.service.CategoriaService;
import br.com.dpplus.fintech.service.LancamentoService;
import br.com.dpplus.fintech.service.RelatorioService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TesteHeranca {

    public static void main(String[] args) {
        PessoaFisica pf = new PessoaFisica(101L, "Ana Silva", "12345678901");
        PessoaJuridica pj = new PessoaJuridica(202L, "Tech Solutions LTDA", "12345678000199");

        var categoriaService = new CategoriaService();
        var lanc = new LancamentoService();
        var rel = new RelatorioService();

        // Categorias e subcategorias
        Categoria catSalarios = categoriaService.criarCategoria("Salários", TipoCategoria.RECEITA);
        Categoria catVendas   = categoriaService.criarCategoria("Vendas",   TipoCategoria.RECEITA);
        Subcategoria scSal    = categoriaService.criarSubcategoria("Salário",                 catSalarios);
        Subcategoria scVend   = categoriaService.criarSubcategoria("Contratos/Serviços",      catVendas);

        Categoria catMoradia  = categoriaService.criarCategoria("Moradia",    TipoCategoria.DESPESA);
        Categoria catTransp   = categoriaService.criarCategoria("Transporte", TipoCategoria.DESPESA);
        Categoria catTech     = categoriaService.criarCategoria("Tecnologia", TipoCategoria.DESPESA);
        Subcategoria scAlug   = categoriaService.criarSubcategoria("Aluguel",        catMoradia);
        Subcategoria scApp    = categoriaService.criarSubcategoria("Aplicativos",    catTransp);
        Subcategoria scSaaS   = categoriaService.criarSubcategoria("Softwares/SaaS", catTech);

        YearMonth mes = YearMonth.now();
        LocalDate hoje = LocalDate.now();

        // PF: receitas e despesas
        lanc.registrarReceita(new Receita(
                pf.getIdUsuario(), "Salário", new BigDecimal("8000"),
                mes.atDay(5), true, false
        ), scSal);

        lanc.registrarDespesa(new Despesa(
                pf.getIdUsuario(), "Aluguel", new BigDecimal("2500"),
                mes.atDay(8), true, true
        ), scAlug);

        lanc.registrarDespesa(new Despesa(
                pf.getIdUsuario(), "Corridas de app", new BigDecimal("120"),
                hoje, false, false
        ), scApp);

        lanc.registrarDespesa(new Despesa(
                pf.getIdUsuario(), "Assinaturas SaaS", new BigDecimal("79.90"),
                hoje.minusDays(2), true, false
        ), scSaaS);

        // PJ: receitas e despesas
        lanc.registrarReceita(new Receita(
                pj.getIdUsuario(), "Contrato de consultoria", new BigDecimal("15000"),
                mes.atDay(10), false, false
        ), scVend);

        lanc.registrarDespesa(new Despesa(
                pj.getIdUsuario(), "Servidores cloud", new BigDecimal("900"),
                mes.atDay(12), true, false
        ), scSaaS);

        lanc.registrarDespesa(new Despesa(
                pj.getIdUsuario(), "Deslocamentos", new BigDecimal("300"),
                mes.atDay(14), false, false
        ), scApp);

        // Resumos PF
        var recPF = lanc.receitasDoUsuario(pf.getIdUsuario());
        var desPF = lanc.despesasDoUsuario(pf.getIdUsuario());
        System.out.println("\n=== RESUMO PF (" + pf.getNome() + ") — " + mes + " ===");
        BigDecimal saldoPF = imprimirResumo(rel, recPF, desPF, mes);
        Map<Categoria, BigDecimal> porCatPF = tentarQuebraPorCategoria(rel, desPF, mes);
        sugerirPF(saldoPF, porCatPF).forEach(s -> System.out.println("- " + s));

        // Resumos PJ
        var recPJ = lanc.receitasDoUsuario(pj.getIdUsuario());
        var desPJ = lanc.despesasDoUsuario(pj.getIdUsuario());
        System.out.println("\n=== RESUMO PJ (" + pj.getRazaoSocial() + ") — " + mes + " ===");
        BigDecimal saldoPJ = imprimirResumo(rel, recPJ, desPJ, mes);
        Map<Categoria, BigDecimal> porCatPJ = tentarQuebraPorCategoria(rel, desPJ, mes);
        sugerirPJ(saldoPJ, porCatPJ).forEach(s -> System.out.println("- " + s));

        System.out.println("\n[OK] Teste finalizado.");
    }

    private static BigDecimal imprimirResumo(RelatorioService rel, List<Receita> receitas, List<Despesa> despesas, YearMonth mes) {
        BigDecimal totR = receitas.stream()
                .filter(r -> YearMonth.from(r.getDataEntrada()).equals(mes))
                .map(Receita::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totD = despesas.stream()
                .filter(d -> YearMonth.from(d.getDataVencimento()).equals(mes))
                .map(Despesa::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldo = totR.subtract(totD);

        System.out.println("Receitas: " + totR);
        System.out.println("Despesas: " + totD);
        System.out.println("Saldo:    " + saldo);

        Map<Categoria, BigDecimal> porCat = tentarQuebraPorCategoria(rel, despesas, mes);
        if (!porCat.isEmpty()) {
            System.out.println("Despesas por categoria:");
            porCat.forEach((c, v) -> System.out.println(" • " + c.getNome() + ": " + v));
        }
        return saldo;
    }

    private static Map<Categoria, BigDecimal> tentarQuebraPorCategoria(RelatorioService rel, List<Despesa> despesas, YearMonth mes) {
        try {
            return rel.totalPorCategoria(despesas, mes);
        } catch (Throwable t) {
            return Map.of();
        }
    }

    // ---------- Sugestões baseadas na lógica do programa ----------

    private static List<String> sugerirPF(BigDecimal saldo, Map<Categoria, BigDecimal> porCat) {
        var out = new ArrayList<String>();
        saldo = saldo == null ? BigDecimal.ZERO : saldo;

        BigDecimal essenciais = somaCategorias(porCat, "Moradia", "Transporte");
        BigDecimal total = porCat.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal pesoEssenciais = total.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
                : essenciais.multiply(new BigDecimal("100")).divide(total, 2, BigDecimal.ROUND_HALF_UP);

        if (saldo.compareTo(new BigDecimal("500")) < 0) {
            out.add("Priorize reduzir gastos essenciais (" + pesoEssenciais + "% do total).");
            out.add("Forme reserva de emergência em Tesouro Selic/CDB D+0 antes de investir.");
            return out;
        }
        if (saldo.compareTo(new BigDecimal("3000")) < 0) {
            out.add("Direcione 70% para reserva (Tesouro Selic/CDB liquidez) e 30% prefixado/IPCA curtos.");
        } else if (saldo.compareTo(new BigDecimal("8000")) < 0) {
            out.add("50% renda fixa curta, 30% prefixado/IPCA, 20% multimercado conservador.");
        } else {
            out.add("40% renda fixa (Selic/IPCA), 30% multimercado, 30% ações/ETFs.");
        }
        if (pesoEssenciais.compareTo(new BigDecimal("60")) > 0) {
            out.add("Revise aluguel/transporte para liberar caixa e elevar aportes.");
        }
        return out;
    }

    private static List<String> sugerirPJ(BigDecimal saldo, Map<Categoria, BigDecimal> porCat) {
        var out = new ArrayList<String>();
        saldo = saldo == null ? BigDecimal.ZERO : saldo;

        BigDecimal tech = somaCategorias(porCat, "Tecnologia");
        BigDecimal total = porCat.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal pesoTech = total.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO
                : tech.multiply(new BigDecimal("100")).divide(total, 2, BigDecimal.ROUND_HALF_UP);

        if (saldo.compareTo(new BigDecimal("2000")) < 0) {
            out.add("Fortaleça capital de giro em renda fixa pós-fixada (D+0/D+1).");
            return out;
        }
        if (saldo.compareTo(new BigDecimal("10000")) < 0) {
            out.add("60% caixa (Selic/CDB), 40% prefixado/IPCA de curto prazo.");
        } else if (saldo.compareTo(new BigDecimal("30000")) < 0) {
            out.add("50% caixa, 30% prefixado/IPCA, 20% multimercado baixa volatilidade.");
        } else {
            out.add("40% caixa, 30% renda fixa média, 20% multimercado, 10% inovação/tecnologia.");
        }
        if (pesoTech.compareTo(new BigDecimal("40")) > 0) {
            out.add("Custos de tecnologia elevados (" + pesoTech + "%). Avalie otimizar licenças/infra.");
        }
        return out;
    }

    private static BigDecimal somaCategorias(Map<Categoria, BigDecimal> mapa, String... nomes) {
        if (mapa == null || mapa.isEmpty()) return BigDecimal.ZERO;
        BigDecimal tot = BigDecimal.ZERO;
        for (var e : mapa.entrySet()) {
            for (String n : nomes) {
                if (e.getKey().getNome().equalsIgnoreCase(n)) {
                    tot = tot.add(e.getValue());
                }
            }
        }
        return tot;
    }
}