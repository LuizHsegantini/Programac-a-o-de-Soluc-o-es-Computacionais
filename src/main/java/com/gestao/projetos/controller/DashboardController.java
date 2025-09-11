package com.gestao.projetos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.gestao.projetos.dao.ProjetoDAO;
import com.gestao.projetos.dao.TarefaDAO;
import com.gestao.projetos.dao.UsuarioDAO;
import com.gestao.projetos.model.Projeto;
import com.gestao.projetos.model.Tarefa;
import com.gestao.projetos.util.SessionManager;
import com.gestao.projetos.MainApp;

import java.util.List;

/**
 * Controller para o dashboard (tela inicial)
 */
public class DashboardController {

    @FXML
    private Label lblTotalProjetos;
    @FXML
    private Label lblProjetosAndamento;
    @FXML
    private Label lblTotalTarefas;
    @FXML
    private Label lblTarefasPendentes;
    @FXML
    private Label lblTarefasEmExecucao;
    @FXML
    private Label lblTotalUsuarios;

    @FXML
    private TableView<Projeto> tblProjetosRecentes;
    @FXML
    private TableColumn<Projeto, String> colProjetoNome;
    @FXML
    private TableColumn<Projeto, String> colProjetoStatus;
    @FXML
    private TableColumn<Projeto, String> colProjetoGerente;
    @FXML
    private TableColumn<Projeto, Double> colProjetoProgresso;

    @FXML
    private TableView<Tarefa> tblTarefasRecentes;
    @FXML
    private TableColumn<Tarefa, String> colTarefaTitulo;
    @FXML
    private TableColumn<Tarefa, String> colTarefaProjeto;
    @FXML
    private TableColumn<Tarefa, String> colTarefaStatus;
    @FXML
    private TableColumn<Tarefa, String> colTarefaResponsavel;

    private ProjetoDAO projetoDAO = new ProjetoDAO();
    private TarefaDAO tarefaDAO = new TarefaDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    private void initialize() {
        setupTables();
        loadDashboardData();
    }

    /**
     * Configura as tabelas do dashboard
     */
    private void setupTables() {
        // Tabela de projetos
        colProjetoNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colProjetoStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colProjetoGerente.setCellValueFactory(new PropertyValueFactory<>("gerenteNome"));
        colProjetoProgresso.setCellValueFactory(new PropertyValueFactory<>("percentualConclusao"));

        // Tabela de tarefas
        colTarefaTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colTarefaProjeto.setCellValueFactory(new PropertyValueFactory<>("projetoNome"));
        colTarefaStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colTarefaResponsavel.setCellValueFactory(new PropertyValueFactory<>("responsavelNome"));
    }

    /**
     * Carrega os dados do dashboard
     */
    private void loadDashboardData() {
        try {
            loadStatistics();
            loadRecentProjects();
            loadRecentTasks();
        } catch (Exception e) {
            e.printStackTrace();
            MainApp.showError("Erro", "Erro ao carregar dados do dashboard: " + e.getMessage());
        }
    }

    /**
     * Carrega estatísticas gerais
     */
    private void loadStatistics() throws Exception {
        // Projetos
        List<Projeto> projetos = projetoDAO.findAll();
        lblTotalProjetos.setText(String.valueOf(projetos.size()));

        long projetosAndamento = projetos.stream()
                .filter(p -> p.getStatus() == Projeto.Status.EM_ANDAMENTO)
                .count();
        lblProjetosAndamento.setText(String.valueOf(projetosAndamento));

        // Tarefas
        List<Tarefa> tarefas;
        if (SessionManager.isColaborador()) {
            // Colaborador vê apenas suas tarefas
            tarefas = tarefaDAO.findByResponsavel(SessionManager.getUsuarioLogado().getId());
        } else {
            // Administrador e gerente veem todas
            tarefas = tarefaDAO.findAll();
        }

        lblTotalTarefas.setText(String.valueOf(tarefas.size()));

        long tarefasPendentes = tarefas.stream()
                .filter(t -> t.getStatus() == Tarefa.Status.PENDENTE)
                .count();
        lblTarefasPendentes.setText(String.valueOf(tarefasPendentes));

        long tarefasEmExecucao = tarefas.stream()
                .filter(t -> t.getStatus() == Tarefa.Status.EM_EXECUCAO)
                .count();
        lblTarefasEmExecucao.setText(String.valueOf(tarefasEmExecucao));

        // Usuários (apenas para admin)
        if (SessionManager.isAdministrador()) {
            List<com.gestao.projetos.model.Usuario> usuarios = usuarioDAO.findAll();
            lblTotalUsuarios.setText(String.valueOf(usuarios.size()));
        } else {
            lblTotalUsuarios.setText("-");
        }
    }

    /**
     * Carrega projetos recentes
     */
    private void loadRecentProjects() throws Exception {
        List<Projeto> projetos;

        if (SessionManager.isGerente() && !SessionManager.isAdministrador()) {
            // Gerente vê apenas seus projetos
            projetos = projetoDAO.findByGerente(SessionManager.getUsuarioLogado().getId());
        } else if (SessionManager.isAdministrador()) {
            // Administrador vê todos
            projetos = projetoDAO.findProjetosResumo();
        } else {
            // Colaborador vê projetos onde tem tarefas
            projetos = projetoDAO.findAll();
        }

        // Mostra apenas os 5 mais recentes
        ObservableList<Projeto> projetosRecentes = FXCollections.observableArrayList(
                projetos.stream().limit(5).toList());

        tblProjetosRecentes.setItems(projetosRecentes);
    }

    /**
     * Carrega tarefas recentes
     */
    private void loadRecentTasks() throws Exception {
        List<Tarefa> tarefas;

        if (SessionManager.isColaborador()) {
            // Colaborador vê apenas suas tarefas
            tarefas = tarefaDAO.findByResponsavel(SessionManager.getUsuarioLogado().getId());
        } else {
            // Administrador e gerente veem todas
            tarefas = tarefaDAO.findAll();
        }

        // Mostra apenas as 5 mais recentes
        ObservableList<Tarefa> tarefasRecentes = FXCollections.observableArrayList(
                tarefas.stream().limit(5).toList());

        tblTarefasRecentes.setItems(tarefasRecentes);
    }
}