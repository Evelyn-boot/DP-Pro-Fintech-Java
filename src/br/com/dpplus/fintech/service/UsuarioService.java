package br.com.dpplus.fintech.service;

import br.com.dpplus.fintech.domain.*;
import br.com.dpplus.fintech.domain.enums.TipoUsuario;

import java.util.ArrayList;
import java.util.List;

public class UsuarioService {
    private final List<Usuario> usuarios = new ArrayList<>();

    public Usuario criarUsuarioPF(String nome, String cpf, String email, String senha) {
        var u = new Usuario(TipoUsuario.PF, email, senha);
        u.definirPessoaFisica(new PessoaFisica(u.getIdUsuario(), nome, cpf));
        usuarios.add(u);
        return u;
    }

    public Usuario criarUsuarioPJ(String razao, String cnpj, String email, String senha) {
        var u = new Usuario(TipoUsuario.PJ, email, senha);
        u.definirPessoaJuridica(new PessoaJuridica(u.getIdUsuario(), cnpj, razao));
        usuarios.add(u);
        return u;
    }

    public Usuario autenticar(String email, String senha) {
        if (email == null || senha == null) return null;
        return usuarios.stream()
                .filter(u -> email.equalsIgnoreCase(u.getEmail()) && senha.equals(u.getSenha()))
                .findFirst()
                .orElse(null);
    }

    public void definirEndereco(Usuario u, Endereco e) { u.definirEndereco(e); }
    public void adicionarTelefone(Usuario u, Telefone t) { u.adicionarTelefone(t); }
}