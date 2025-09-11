package com.gestao.projetos.model;

import java.time.LocalDateTime;

/**
 * Classe modelo para representar um usuário do sistema
 */
public class Usuario {

    public enum Perfil {
        ADMINISTRADOR("Administrador"),
        GERENTE("Gerente"),
        COLABORADOR("Colaborador");

        private final String descricao;

        Perfil(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    private Integer id;
    private String nome;
    private String email;
    private String login;
    private String senha;
    private Perfil perfil;
    private boolean ativo;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;

    // Construtores
    public Usuario() {
        this.ativo = true;
        this.dataCadastro = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    public Usuario(String nome, String email, String login, String senha, Perfil perfil) {
        this();
        this.nome = nome;
        this.email = email;
        this.login = login;
        this.senha = senha;
        this.perfil = perfil;
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public void setPerfil(Perfil perfil) {
        this.perfil = perfil;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    // Métodos utilitários
    public boolean isAdministrador() {
        return perfil == Perfil.ADMINISTRADOR;
    }

    public boolean isGerente() {
        return perfil == Perfil.GERENTE;
    }

    public boolean isColaborador() {
        return perfil == Perfil.COLABORADOR;
    }

    @Override
    public String toString() {
        return nome + " (" + perfil.getDescricao() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Usuario usuario = (Usuario) obj;
        return id != null && id.equals(usuario.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}

  hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
