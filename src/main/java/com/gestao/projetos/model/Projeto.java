package com.gestao.projetos.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Classe modelo para representar um projeto
 */
public class Projeto {

    public enum Status {
        PLANEJADO("Planejado"),
        EM_ANDAMENTO("Em Andamento"),
        CONCLUIDO("Concluído"),
        CANCELADO("Cancelado");

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

    private Integer id;
    private String nome;
    private String descricao;
    private LocalDate dataInicio;
    private LocalDate dataPrevistaTermino;
    private LocalDate dataTerminoReal;
    private Status status;
    private Integer gerenteId;
    private String gerenteNome; // Para exibição
    private boolean ativo;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;

    // Campos calculados para relatórios
    private Integer totalTarefas;
    private Integer tarefasConcluidas;
    private Double percentualConclusao;

    // Construtores
    public Projeto() {
        this.ativo = true;
        this.status = Status.PLANEJADO;
        this.dataCadastro = LocalDateTime.now();
        this.dataAtualizacao = LocalDateTime.now();
    }

    public Projeto(String nome, String descricao, LocalDate dataInicio,
            LocalDate dataPrevistaTermino, Integer gerenteId) {
        this();
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataPrevistaTermino = dataPrevistaTermino;
        this.gerenteId = gerenteId;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataPrevistaTermino() {
        return dataPrevistaTermino;
    }

    public void setDataPrevistaTermino(LocalDate dataPrevistaTermino) {
        this.dataPrevistaTermino = dataPrevistaTermino;
    }

    public LocalDate getDataTerminoReal() {
        return dataTerminoReal;
    }

    public void setDataTerminoReal(LocalDate dataTerminoReal) {
        this.dataTerminoReal = dataTerminoReal;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getGerenteId() {
        return gerenteId;
    }

    public void setGerenteId(Integer gerenteId) {
        this.gerenteId = gerenteId;
    }

    public String getGerenteNome() {
        return gerenteNome;
    }

    public void setGerenteNome(String gerenteNome) {
        this.gerenteNome = gerenteNome;
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

    public Integer getTotalTarefas() {
        return totalTarefas;
    }

    public void setTotalTarefas(Integer totalTarefas) {
        this.totalTarefas = totalTarefas;
    }

    public Integer getTarefasConcluidas() {
        return tarefasConcluidas;
    }

    public void setTarefasConcluidas(Integer tarefasConcluidas) {
        this.tarefasConcluidas = tarefasConcluidas;
    }

    public Double getPercentualConclusao() {
        return percentualConclusao;
    }

    public void setPercentualConclusao(Double percentualConclusao) {
        this.percentualConclusao = percentualConclusao;
    }

    // Métodos utilitários
    public boolean isCancelado() {
        return status == Status.CANCELADO;
    }

    public boolean isConcluido() {
        return status == Status.CONCLUIDO;
    }

    @Override
    public String toString() {
        return nome + " (" + status.getDescricao() + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Projeto projeto = (Projeto) obj;
        return id != null && id.equals(projeto.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
