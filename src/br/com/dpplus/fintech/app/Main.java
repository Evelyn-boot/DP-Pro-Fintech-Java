package br.com.dpplus.fintech.app;

import br.com.dpplus.fintech.domain.*;
import br.com.dpplus.fintech.domain.enums.TipoCategoria;
import br.com.dpplus.fintech.service.CategoriaService;
import br.com.dpplus.fintech.service.LancamentoService;
import br.com.dpplus.fintech.service.RelatorioService;
import br.com.dpplus.fintech.service.UsuarioService;
import br.com.dpplus.fintech.util.Console;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final UsuarioService usuarioService   = new UsuarioService();
    private static final CategoriaService categoriaService = new CategoriaService();
    private static final LancamentoService lancService     = new LancamentoService();
    private static final RelatorioService relatorioService = new RelatorioService();

    private static Usuario usuarioLogado;

    public static void main(String[] args) {
        Console.title("Bem vindo(a) ao projeto \n\uD83D\uDCCAFintech DPPlus!\uD83D\uDE80");

        // Cadastro inicial PF/PJ ou Login
        if (Console.askYesNo("Já possui cadastro?")) {
            login();
        } else {
            String tipo = Console.askRequired("Deseja cadastrar (PF) Pessoa Física ou (PJ) Pessoa Jurídica?");
            if (tipo.equalsIgnoreCase("PF")) {
                cadastrarPFSimploes();
                Console.ok(" Cadastro de Pessoa Física concluído!\uD83D\uDC64");
            } else if (tipo.equalsIgnoreCase("PJ")) {
                cadastrarPJSimploes();
                Console.ok(" Cadastro de Pessoa Jurídica concluído!\uD83D\uDCCA");
            } else {
                Console.error("Opção inválida! Digite apenas PF ou PJ.");
                System.exit(0);
            }
            Console.ok("Cadastro concluído. Faça o login.\uD83D\uDD10");
            login();
        }

        // Menu principal
        menuPrincipal();
    }

    // ===== Cadastro/Login =====

    private static void cadastrarPFSimploes() {
        Console.title("Cadastro PF");
        String nome  = Console.askRequired("Nome:");
        String cpf   = Console.askRequired("CPF (somente números, 11 dígitos):");
        String email = Console.askEmail("E-mail:");
        String senha = Console.askRequired("Senha:");
        usuarioLogado = usuarioService.criarUsuarioPF(nome, cpf, email, senha);
    }

    private static void cadastrarPJSimploes() {
        Console.title("Cadastro PJ");
        String razao = Console.askRequired("Razão Social:");
        String cnpj  = Console.askRequired("CNPJ (somente números, 14 dígitos):");
        String email = Console.askEmail("E-mail:");
        String senha = Console.askRequired("Senha:");
        usuarioLogado = usuarioService.criarUsuarioPJ(razao, cnpj, email, senha);
    }

    private static void login() {
        Console.title("Login");
        while (true) {
            String email = Console.askEmail("E-mail:");
            String senha = Console.askRequired("Senha:");
            usuarioLogado = usuarioService.autenticar(email, senha);
            if (usuarioLogado != null) {
                String primeiroNome = email.split("@")[0].split("[._-]")[0];
                Console.ok("Login efetuado com sucesso, " + Console.cap(primeiroNome) + "!");
                break;
            }
            Console.error("Credenciais inválidas.");
            if (!Console.askYesNo("Tentar novamente?")) System.exit(0);
        }
    }

    // ===== Menus =====

    private static void menuPrincipal() {
        while (true) {
            Console.title("Menu Principal");
            Console.line("1) \uD83D\uDD20Categorias/Subcategorias");
            Console.line("2) \uD83D\uDCC8Cadastrar Receita");
            Console.line("3) \uD83D\uDCC9Cadastrar Despesa");
            Console.line("4) \uD83D\uDCCARelatórios do mês atual \nE sugestões de investimentos");
            Console.line("5) \uD83D\uDC63Sair");
            var op = Console.askRequired("Escolha:");
            switch (op) {
                case "1" -> menuCategorias();
                case "2" -> cadastrarReceita();
                case "3" -> cadastrarDespesa();
                case "4" -> relatoriosMesAtual();
                case "5" -> { Console.ok("Até breve!\uD83D\uDC4B\uD83C\uDFFC"); return; }
                default -> Console.error("Opção inválida.");
            }
        }
    }

    private static void menuCategorias() {
        while (true) {
            Console.title("Categorias/Subcategorias");
            Console.line("1) Criar Categoria");
            Console.line("2) Criar Subcategoria");
            Console.line("3) Listar Categorias");
            Console.line("4) Voltar");
            var op = Console.askRequired("Escolha:");
            switch (op) {
                case "1" -> criarCategoria();
                case "2" -> criarSubcategoria();
                case "3" -> listarCategorias();
                case "4" -> { return; }
                default -> Console.error("Opção inválida.");
            }
        }
    }

    private static void criarCategoria() {
        String nome = Console.askRequired("Nome da categoria:");
        boolean isRec = Console.askYesNo("Categoria de RECEITA? (não: DESPESA)");
        var tipo = isRec ? TipoCategoria.RECEITA : TipoCategoria.DESPESA;
        categoriaService.criarCategoria(nome, tipo);
        Console.ok("Categoria criada!");
    }

    private static void criarSubcategoria() {
        boolean isRec = Console.askYesNo("Subcategoria para RECEITA? (não: DESPESA)");
        var tipo = isRec ? TipoCategoria.RECEITA : TipoCategoria.DESPESA;
        var cats = categoriaService.listarCategorias(tipo);
        if (cats.isEmpty()) {
            Console.error("Não há categorias do tipo selecionado.");
            return;
        }
        var cat = Console.choose("Escolha a categoria:", cats, Categoria::getNome);
        if (cat == null) return;
        String nome = Console.askRequired("Nome da subcategoria:");
        categoriaService.criarSubcategoria(nome, cat);
        Console.ok("Subcategoria criada!");
    }

    private static void listarCategorias() {
        var todas = categoriaService.listarTodasCategorias();
        if (todas.isEmpty()) {
            Console.info("Nenhuma categoria cadastrada.");
            return;
        }
        Console.title("Categorias");
        todas.forEach(c -> System.out.println("- " + c));
    }

    // ===== Lançamentos =====

    private static void cadastrarReceita() {
        Console.title("Cadastrar Receita\uD83D\uDCC8");

        String desc       = Console.askRequired("Descrição:");
        BigDecimal valor  = Console.askMoney("Valor (ex.: 300,00):");
        LocalDate data    = Console.askDateBR("Data de entrada");
        boolean recorrente = Console.askYesNo("Recorrente?");
        boolean pendente   = Console.askYesNo("Pendente?");

        Categoria cat = escolherOuCriarCategoriaObrigatoria(TipoCategoria.RECEITA);
        Subcategoria sc = escolherOuCriarSubcategoriaOpcional(cat);

        Receita r = new Receita(
                usuarioLogado.getIdUsuario(),
                desc, valor, data, recorrente, pendente
        );
        if (sc != null) r.setSubcategoria(sc); else r.setCategoria(cat);

        lancService.registrarReceita(r);
        Console.ok("Receita salva!");
    }

    private static void cadastrarDespesa() {
        Console.title("Cadastrar Despesa\uD83D\uDCC9");

        String desc        = Console.askRequired("Descrição:");
        BigDecimal valor   = Console.askMoney("Valor (ex.: 300,00):");
        LocalDate venc     = Console.askDateBR("Data de vencimento");
        boolean recorrente = Console.askYesNo("Recorrente?");
        boolean pendente   = Console.askYesNo("Pendente?");

        Categoria cat = escolherOuCriarCategoriaObrigatoria(TipoCategoria.DESPESA);
        Subcategoria sc = escolherOuCriarSubcategoriaOpcional(cat);

        Despesa d = new Despesa(
                usuarioLogado.getIdUsuario(),
                desc, valor, venc, recorrente, pendente
        );
        if (sc != null) d.setSubcategoria(sc); else d.setCategoria(cat);

        lancService.registrarDespesa(d);
        Console.ok("Despesa salva!");
    }

    private static Categoria escolherOuCriarCategoriaObrigatoria(TipoCategoria tipo) {
        var cats = categoriaService.listarCategorias(tipo);
        if (cats.isEmpty()) {
            Console.info("Não há categorias do tipo " + tipo + ". Vamos criar agora.");
            String nome = Console.askRequired("Nome da nova categoria:");
            return categoriaService.criarCategoria(nome, tipo);
        } else {
            if (Console.askYesNo("Deseja criar uma NOVA categoria?")) {
                String nome = Console.askRequired("Nome da nova categoria:");
                return categoriaService.criarCategoria(nome, tipo);
            }
            var cat = Console.choose("Escolha a categoria:", cats, Categoria::getNome);
            if (cat == null) {
                Console.error("Categoria é obrigatória. Criando uma nova.");
                String nome = Console.askRequired("Nome da nova categoria:");
                return categoriaService.criarCategoria(nome, tipo);
            }
            return cat;
        }
    }

    private static Subcategoria escolherOuCriarSubcategoriaOpcional(Categoria cat) {
        if (!Console.askYesNo("Deseja vincular uma subcategoria?")) return null;

        var subs = categoriaService.listarSubcategorias(cat);
        if (subs.isEmpty()) {
            Console.info("Não há subcategorias para '" + cat.getNome() + "'. Vamos criar agora.");
            String nome = Console.askRequired("Nome da nova subcategoria:");
            return categoriaService.criarSubcategoria(nome, cat);
        } else {
            if (Console.askYesNo("Deseja criar uma NOVA subcategoria?")) {
                String nome = Console.askRequired("Nome da nova subcategoria:");
                return categoriaService.criarSubcategoria(nome, cat);
            }
            return Console.choose("Escolha a subcategoria:", subs, Subcategoria::getNome);
        }
    }

    // ===== Relatórios =====

    private static void relatoriosMesAtual() {
        var mes = YearMonth.from(LocalDate.now());
        var receitas = lancService.receitasDoUsuario(usuarioLogado.getIdUsuario());
        var despesas = lancService.despesasDoUsuario(usuarioLogado.getIdUsuario());

        var misto = new ArrayList<Object>();
        misto.addAll(despesas);
        misto.addAll(receitas);

        Console.title("Relatórios - " + mes);
        var porCat = relatorioService.totalPorCategoria(misto, mes);
        Console.line("Totais por Categoria:");
        porCat.forEach((c, v) -> System.out.println("- " + c.getNome() + ": " + v));

        var porSub = relatorioService.totalPorSubcategoria(misto, mes);
        Console.line("Totais por Subcategoria:");
        porSub.forEach((s, v) -> System.out.println("- " + s.getCategoria().getNome() + " > " + s.getNome() + ": " + v));

        List<String> dicas = relatorioService.sugestoesInvestimento(receitas, despesas, mes);
        Console.line("\n💡 Sugestões:");
        dicas.forEach(d -> System.out.println("- " + d));
    }
}