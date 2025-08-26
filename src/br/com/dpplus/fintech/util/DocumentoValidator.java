package br.com.dpplus.fintech.util;

/** Validações básicas (formato) para CPF e CNPJ.
 *  Regras: manter somente dígitos e validar o tamanho: CPF=11, CNPJ=14.
 *  (Sem checagem de dígitos verificadores neste projeto.)
 */
public final class DocumentoValidator {
    private DocumentoValidator() {}

    /** Remove tudo que não for dígito. */
    public static String soDigitos(String s) {
        return s == null ? "" : s.replaceAll("\\D+", "");
    }

    public static boolean isCpfBasicoValido(String cpf) {
        String d = soDigitos(cpf);
        return d.length() == 11;
    }

    public static boolean isCnpjBasicoValido(String cnpj) {
        String d = soDigitos(cnpj);
        return d.length() == 14;
    }
}