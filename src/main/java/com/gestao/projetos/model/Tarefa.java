package com.gestao.projetos.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Classe modelo para representar uma tarefa
 */
public class Tarefa {
    
    public enum Status {
        PENDENTE("Pendente"),
        EM_EXECUCAO("Em Execução"),
        CONCLUIDA("Concluída"),
        CANCELADA("Cancelada");
        
        private final String descricao;
        
        Status(String descricao) {
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
    
    public enum Prioridade {
        BAIXA("Baixa"),
        MEDIA("Média"),
        ALTA("Alta");
        
        private final String descricao;
        
        Prioridade(String descricao) {
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
    private String titulo;
    private String descricao;
    private Integer projetoId;
    private String projetoNome; // Para exibição
    private Integer responsavelId;
    private String responsavelNome; // Para exibição
    private Status status;
    private Prioridade prioridade;
    private LocalDate dataPrevistaConclusao;
    private LocalDate dataConclusaoReal;
    private boolean ativo;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;
    
    // Construtores
    public Tarefa() {
        this.ativo = true;
        this.status = Status.PENDENTE;
        this.prioridade = Prioridade.MEDIA;
        this.dataCadastro = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }
    
    public Tarefa(String titulo, String descricao, Integer projetoId, 
                  Integer responsavelId, LocalDate dataPrevistaConclusao) {
        this();
        this.titulo = titulo;
        this.descricao = descricao;
        this.projetoId = projetoId;
        this.responsavelId = responsavelId;
        this.dataPrevistaConclusao = dataPrevistaConclusao;
    }
    
    // Getters e Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public Integer getProjetoId() {
        return projetoId;
    }
    
    public void setProjetoId(Integer projetoId) {
        this.projetoId = projetoId;
    }
    
    public String getProjetoNome() {
        return projetoNome;
    }
    
    public void setProjetoNome(String projetoNome) {
        this.projetoNome = projetoNome;
    }
    
    public Integer getResponsavelId() {
        return responsavelId;
    }
    
    public void setResponsavelId(Integer responsavelId) {
        this.responsavelId = responsavelId;
    }
    
    public String getResponsavelNome() {
        return responsavelNome;
    }
    
    public void setResponsavelNome(String responsavelNome) {
        this.responsavelNome = responsavelNome;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public Prioridade getPrioridade() {
        return prioridade;
    }
    
    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }
    
    public LocalDate getDataPrevistaConclusao() {
        return dataPrevistaConclusao;
    }
    
    public void setDataPrevistaConclusao(LocalDate dataPrevistaConclusao) {
        this.dataPrevistaConclusao = dataPrevistaConclusao;
    }
    
    public LocalDate getDataConclusaoReal() {
        return dataConclusaoReal;
    }
    
    public void setDataConclusaoReal(LocalDate dataConclusaoReal) {
        this.dataConclusaoReal = dataConclusaoReal;
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
    public boolean isConcluida() {
        return status == Status.CONCLUIDA;
    }
    
    public boolean isCancelada() {
        return status == Status.CANCELADA;
    }
    
    @Override
    public String toString() {
        return titulo + " (" + status.getDescricao() + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tarefa tarefa = (Tarefa) obj;
        return id != null && id.equals(tarefa.id);
    }
    
    @Override
    public int