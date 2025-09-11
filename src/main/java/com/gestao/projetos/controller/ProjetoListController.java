package com.gestao.projetos.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.gestao.projetos.MainApp;
import com.gestao.projetos.dao.ProjetoDAO;
import com.gestao.projetos.model.Projeto;
import com.gestao.projetos.util.SessionManager;

import java.util.List;

/**
 * Controller para listagem de projetos
 */
public class ProjetoListController {

    @FXML
    private TableView<Projeto> tblProjetos;
    @FXML
    private TableColumn<Projeto, String> colNome;
    @FXML
    private TableColumn<Projeto, String> colDescricao;
    @FXML
    private TableColumn<Projeto, String> colStatus;
    @FXML
    private TableColumn<Projeto, String> colGerente;
    @FXML
    private TableColumn<Projeto, String> colDataInicio;
    @FXML
    private TableColumn<Projeto, Void> colAcoes;

    private ProjetoDAO projetoDAO = new ProjetoDAO();
    private ObservableList<Projeto> projetos = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        setupTable();
        loadProjetos();
    }

    /**
     * Configura a tabela de projetos
     */
    private void setupTable() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colStatus.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStatus().getDescricao()));
        colGerente.setCellValueFactory(new PropertyValueFactory<>("gerenteNome"));
        colDataInicio.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDataInicio() != null ? cellData.getValue().getDataInicio().toString() : ""));

        // Coluna de ações
        colAcoes.setCellFactory(param -> new TableCell<Projeto, Void>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnCancelar = new Button("Cancelar");
            private final javafx.scene.layout.HBox pane = new javafx.scene.layout.HBox(5, btnEditar, btnCancelar);

            {
                btnEditar.setOnAction(event -> {
                    Projeto projeto = getTableView().getItems().get(getIndex());
                    handleEditar(projeto);
                });

                btnCancelar.setOnAction(event -> {
                    Projeto projeto = getTableView().getItems().get(getIndex());
                    handleCancelar(projeto);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Projeto projeto = getTableView().getItems().get(getIndex());

                    // Verifica permissões
                    boolean canEdit = SessionManager.isAdministrador() ||
                            SessionManager.isGerenteOfProject(projeto.getGerenteId());

                    btnEditar.setDisable(!canEdit);
                    btnCancelar.setDisable(!canEdit || projeto.isCancelado() || projeto.isConcluido());

                    setGraphic(pane);
                }
            }
        });

        tblProjetos.setItems(projetos);
    }

    /**
     * Carrega lista de projetos
     */
    private void loadProjetos() {
        try {
            List<Projeto> lista;

            if (SessionManager.isAdministrador()) {
                lista = projetoDAO.findAll();
            } else {
                // Gerente vê apenas seus projetos
                lista = projetoDAO.findByGerente(SessionManager.getUsuarioLogado().getId());
            }

            projetos.clear();
            projetos.addAll(lista);
        } catch (Exception e) {
            e.printStackTrace();
            MainApp.showError("Erro", "Erro ao carregar projetos: " + e.getMessage());
        }
    }

    @FXML
    private void handleNovo() {
        openProjetoForm(null);
    }

    @FXML
    private void handleRefresh() {
        loadProjetos();
    }

    /**
     * Edita um projeto
     */
    private void handleEditar(Projeto projeto) {
        // Verifica permissão
        if (!SessionManager.isAdministrador() &&
                !SessionManager.isGerenteOfProject(projeto.getGerenteId())) {
            MainApp.showError("Erro", "Você não tem permissão para editar este projeto.");
            return;
        }

        openProjetoForm(projeto);
    }

    /**
     * Cancela um projeto
     */
    private void handleCancelar(Projeto projeto) {
        // Verifica permissão
        if (!SessionManager.isAdministrador() &&
                !SessionManager.isGerenteOfProject(projeto.getGerenteId())) {
            MainApp.showError("Erro", "Você não tem permissão para cancelar este projeto.");
            return;
        }

        if (projeto.isCancelado() || projeto.isConcluido()) {
            MainApp.showError("Erro", "Este projeto já está cancelado ou concluído.");
            return;
        }

        if (MainApp.showConfirmation("Confirmação",
                "Tem certeza que deseja cancelar o projeto '" + projeto.getNome() + "'?\n" +
                        "Todas as tarefas pendentes serão inativadas.")) {
            try {
                projetoDAO.cancelProject(projeto.getId());
                loadProjetos();
                MainApp.showInfo("Sucesso", "Projeto cancelado com sucesso.");
            } catch (Exception e) {
                e.printStackTrace();
                MainApp.showError("Erro", "Erro ao cancelar projeto: " + e.getMessage());
            }
        }
    }

    /**
     * Abre formulário de projeto
     */
    private void openProjetoForm(Projeto projeto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProjetoForm.fxml"));
            Scene scene = new Scene(loader.load());

            ProjetoFormController controller = loader.getController();
            if (projeto != null) {
                controller.setProjeto(projeto);
            }

            Stage stage = new Stage();
            stage.setTitle(projeto == null ? "Novo Projeto" : "Editar Projeto");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(MainApp.getPrimaryStage());
            stage.setResizable(false);

            stage.setOnHidden(e -> loadProjetos());

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            MainApp.showError("Erro", "Erro ao abrir formulário: " + e.getMessage());
        }
    }
}