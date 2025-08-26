package br.com.dpplus.fintech.util;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class Console {
    private static final Scanner SC = new Scanner(System.in);
    private static final DateTimeFormatter BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Pattern EMAIL_RX = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private Console() {}

    // Estética
    public static void title(String t) { System.out.println("\n=== " + t + " ==="); }
    public static void ok(String m)    { System.out.println("✅ " + m); }
    public static void error(String m) { System.out.println("❌ " + m); }
    public static void info(String m)  { System.out.println("ℹ️  " + m); }
    public static void line()          { System.out.println("--------------------------------------------------"); }
    public static void line(String m)  { System.out.println(m); }

    // Auxiliares
    public static String cap(String s) {
        if (s == null || s.isBlank()) return "";
        var base = Normalizer.normalize(s.trim(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return base.substring(0,1).toUpperCase(Locale.ROOT) + base.substring(1).toLowerCase(Locale.ROOT);
    }

    // Inputs
    public static String askRequired(String label) {
        while (true) {
            System.out.print(label + " ");
            var v = SC.nextLine().trim();
            if (!v.isBlank()) return v;
            error("Campo obrigatório.");
        }
    }

    public static String askEmail(String label) {
        while (true) {
            System.out.print(label + " ");
            var v = SC.nextLine().trim();
            if (EMAIL_RX.matcher(v).matches()) return v;
            error("E-mail inválido. Ex.: joao.silva@mail.com");
        }
    }

    public static boolean askYesNo(String label) {
        while (true) {
            System.out.print(label + " (s/n): ");
            var v = SC.nextLine().trim().toLowerCase(Locale.ROOT);
            if (v.equals("s") || v.equals("sim")) return true;
            if (v.equals("n") || v.equals("nao") || v.equals("não")) return false;
            error("Responda com 's' ou 'n'.");
        }
    }

    public static BigDecimal askMoney(String label) {
        while (true) {
            System.out.print(label + " ");
            var v = SC.nextLine().trim().replace(".", "").replace(",", ".");
            try { return new BigDecimal(v); }
            catch (Exception ex) { error("Valor inválido. Ex.: 1200,50"); }
        }
    }

    public static LocalDate askDateBR(String label) {
        while (true) {
            System.out.print(label + " (dd/MM/aaaa): ");
            var v = SC.nextLine().trim();
            try { return LocalDate.parse(v, BR); }
            catch (DateTimeParseException ex) { error("Data inválida. Ex.: 24/08/2025"); }
        }
    }

    // Escolha em lista
    public static <T> T choose(String title, List<T> options, Function<T, String> labeler) {
        if (options == null || options.isEmpty()) return null;
        title(title);
        for (int i = 0; i < options.size(); i++) {
            System.out.printf("%d) %s%n", i + 1, labeler.apply(options.get(i)));
        }
        while (true) {
            System.out.print("Selecione: ");
            var v = SC.nextLine().trim();
            try {
                int idx = Integer.parseInt(v) - 1;
                if (idx >= 0 && idx < options.size()) return options.get(idx);
            } catch (NumberFormatException ignored) {}
            error("Opção inválida.");
        }
    }
}
