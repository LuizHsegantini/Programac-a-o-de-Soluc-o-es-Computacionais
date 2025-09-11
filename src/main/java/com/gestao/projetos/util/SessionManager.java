package com.gestao.projetos.util;

import com.gestao.projetos.model.Usuario;

/**
 * Classe para gerenciar a sessão do usuário logado
 */
public class SessionManager {

    private static Usuario usuarioLogado = null;

    /**
     * Define o usuário logado na sessão
     */
    public static void setUsuarioLogado(Usuario usuario) {
        usuarioLogado = usuario;
    }

    /**
     * Retorna o usuário logado
     */
    public static Usuario getUsuarioLogado() {
        return usuarioLogado;
    }

    /**
     * Verifica se há um usuário logado
     */
    public static boolean isLoggedIn() {
        return usuarioLogado != null;
    }

    /**
     * Limpa a sessão (logout)
     */
    public static void clearSession() {
        usuarioLogado = null;
    }

    /**
     * Verifica se o usuário logado é administrador
     */
    public static boolean isAdministrador() {
        return usuarioLogado != null && usuarioLogado.isAdministrador();
    }

    /**
     * Verifica se o usuário logado é gerente
     */
    public static boolean isGerente() {
        return usuarioLogado != null && usuarioLogado.isGerente();
    }

    /**
     * Verifica se o usuário logado é colaborador
     */
    public static boolean isColaborador() {
        return usuarioLogado != null && usuarioLogado.isColaborador();
    }

    /**
     * Verifica se o usuário logado tem permissão de gerente ou superior
     */
    public static boolean hasGerentePermission() {
        return usuarioLogado != null &&
                (usuarioLogado.isAdministrador() || usuarioLogado.isGerente());
    }

    /**
     * Verifica se o usuário logado é gerente de um projeto específico
     */
    public static boolean isGerenteOfProject(Integer gerenteId) {
        return usuarioLogado != null && usuarioLogado.getId().equals(gerenteId);
    }
}