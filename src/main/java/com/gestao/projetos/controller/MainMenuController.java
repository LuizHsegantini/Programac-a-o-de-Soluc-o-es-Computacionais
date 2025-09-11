package com.gestao.projetos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.Node;

import com.gestao.projetos.MainApp;
import com.gestao.projetos.util.SessionManager;
import com.gestao.projetos.model.Usuario;

/**
 * Controller para o menu principal
 */
public class MainMenuController {

    @FXML
    private BorderPane rootPane;
    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu menuUsuarios;
    @FXML
    private Menu menuProjetos;
    @FXML
    private Menu menuTarefas;
    @FXML
    private Menu menuRelatorios;
    @FXML
    private Label lblUsuarioLogado;

    @FXML
    private void initialize() {
        Usuario usuario = SessionManager.getUsuarioLogado();
        if (usuario != null) {
            lblUsuarioLogado
                    .setText("Logado como: " + usuario.getNome() + " (" + usuario.getPerfil().getDescricao() + ")");
            configureMenus();
        }

        // Carrega tela inicial (Dashboard)
        loadDashboard();
    }

    /**
     * Configura visibilidade dos menus baseado no perfil do usuário
     */
    private void configureMenus() {
        Usuario usuario = SessionManager.getUsuarioLogado();

        // Menu Usuários - apenas administradores
        menuUsuarios.setVisible(usuario.isAdministrador());

        // Menu Projetos - administradores e gerentes
        menuProjetos.setVisible(usuario.isAdministrador() || usuario.isGerente());

        // Menu Tarefas - todos podem ver
        menuTarefas.setVisible(true);

        // Menu Relatórios - administradores e gerentes
        menuRelatorios.setVisible(usuario.isAdministrador() || usuario.isGerente());
    }

    // ========== MENU USUÁRIOS ==========

    @FXML
    private void handleListarUsuarios() {
        loadContent("/fxml/UsuarioList.fxml");
    }

    @FXML
    private void handleNovoUsuario() {
        loadContent("/fxml/UsuarioForm.fxml");
    }

    // ========== MENU PROJETOS ==========

    @FXML
    private void handleListarProjetos() {
        loadContent("/fxml/ProjetoList.fxml");
    }

    @FXML
    private void handleNovoProjeto() {
        loadContent("/fxml/ProjetoForm.fxml");
    }

    // ========== MENU TAREFAS ==========

    @FXML
    private void handleListarTarefas() {
        loadContent("/fxml/TarefaList.fxml");
    }

    @FXML
    private void handleNovaTarefa() {
        loadContent("/fxml/TarefaForm.fxml");
    }

    @FXML
    private void handleMinhasTarefas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TarefaList.fxml"));
            Node content = loader.load();

            // Passa parâmetro para filtrar apenas tarefas do usuário logado
            TarefaListController controller = loader.getController();
            if (controller != null) {
                controller.loadMinhasTarefas();
            }

            rootPane.setCenter(content);
        } catch (Exception e) {
            e.printStackTrace();
            MainApp.showError("Erro", "Erro ao carregar minhas tarefas: " + e.getMessage());
        }
    }

    // ========== MENU RELATÓRIOS ==========

    @FXML
    private void handleRelatoriosProjetos() {
        loadContent("/fxml/RelatoriosProjetos.fxml");
    }

    @FXML
    private void handleRelatoriosTarefas() {
        loadContent("/fxml/RelatoriosTarefas.fxml");
    }

    // ========== MENU SISTEMA ==========

    @FXML
    private void handleDashboard() {
        loadDashboard();
    }

    @FXML
    private void handleAlterarSenha() {
        loadContent("/fxml/AlterarSenha.fxml");
    }

    @FXML
    private void handleLogout() {
        if (MainApp.showConfirmation("Logout", "Deseja realmente sair do sistema?")) {
            MainApp.showLoginScreen();
        }
    }

    @FXML
    private void handleSobre() {
        MainApp.showInfo("Sobre",
                "Sistema de Gestão de Projetos e Equipes\n" +
                        "Versão 1.0\n" +
                        "Desenvolvido em Java com JavaFX e MySQL\n\n" +
                        "Este sistema permite o gerenciamento de projetos, \n" +
                        "equipes e tarefas com diferentes perfis de acesso.");
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Carrega o dashboard inicial
     */
    private void loadDashboard() {
        loadContent("/fxml/Dashboard.fxml");
    }

    /**
     * Carrega conteúdo no painel central
     */
    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();
            rootPane.setCenter(content);
        } catch (Exception e) {
            e.printStackTrace();
            MainApp.showError("Erro", "Erro ao carregar tela: " + e.getMessage());
        }
    }
}