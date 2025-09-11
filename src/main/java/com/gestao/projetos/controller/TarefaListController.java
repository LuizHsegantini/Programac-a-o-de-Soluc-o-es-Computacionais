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
import com.gestao.projetos.dao.TarefaDAO;
import com.gestao.projetos.model.Tarefa;
import com.gestao.projetos.util.SessionManager;

import java.util.List;

/**
 * Controller para listagem de tarefas
 */
public class TarefaListController {

    @FXML
    private TableView<Tarefa> tblTarefas;
    @FXML
    private TableColumn<Tarefa, String> colTitulo;
    @FXML
    private TableColumn<Tarefa, String> colProjeto;
    @FXML
    private TableColumn<Tarefa, String> colResponsavel;
    @FXML
    private TableColumn<Tarefa, String> colStatus;
    @FXML
    private TableColumn<Tarefa, String> colPrioridade;
    @FXML
    private TableColumn<Tarefa, Void> colAcoes;

    private TarefaDAO tarefaDAO = new TarefaDAO();
    private ObservableList<Tarefa> tarefas = FXCollections.observableArrayList();
    private boolean somenteMinhasTarefas = false;

    @FXML
    private void initialize() {
        setupTable();
        loadTarefas();
    }

    /**
     * Configura visualização para "Minhas Tarefas"
     */
    public void loadMinhasTarefas() {
        somenteMinhasTarefas = true;
        loadTarefas();
    }

    /**
     * Configura a tabela de tarefas
     */
    private void setupTable() {
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colProjeto.setCellValueFactory(new PropertyValueFactory<>("projetoNome"));
        colResponsavel.setCellValueFactory(new PropertyValueFactory<>("responsavelNome"));
        colStatus.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStatus().getDescricao()));
        colPrioridade.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getPrioridade().getDescricao()));

        // Coluna de ações
        colAcoes.setCellFactory(param -> new TableCell<Tarefa, Void>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnStatus = new Button("Status");
            private final javafx.scene.layout.HBox pane = new javafx.scene.layout.HBox(5, btnEditar, btnStatus);

            {
                btnEditar.setOnAction(event -> {
                    Tarefa tarefa = getTableView().getItems().get(getIndex());
                    handleEditar(tarefa);
                });

                btnStatus.setOnAction(event -> {
                    Tarefa tarefa = getTableView().getItems().get(getIndex());
                    handleAlterarStatus(tarefa);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Tarefa tarefa = getTableView().getItems().get(getIndex());

                    try {
                        // Verifica permissões
                        boolean canEdit = tarefaDAO.canUserEditTask(
                                tarefa.getId(),
                                SessionManager.getUsuarioLogado().getId(),
                                SessionManager.isAdministrador(),
                                SessionManager.hasGerentePermission());

                        boolean isResponsavel = tarefa.getResponsavelId().equals(
                                SessionManager.getUsuarioLogado().getId());

                        btnEditar.setDisable(!canEdit);
                        btnStatus.setDisable(!isResponsavel && !SessionManager.isAdministrador());

                    } catch (Exception e) {
                        btnEditar.setDisable(true);
                        btnStatus.setDisable(true);
                    }

                    setGraphic(pane);
                }
            }
        });

        tblTarefas.setItems(tarefas);
    }

    /**
     * Carrega lista de tarefas
     */
    private void loadTarefas() {
        try {
            List<Tarefa> lista;

            if (somenteMinhasTarefas || SessionManager.isColaborador()) {
                // Carrega apenas tarefas do usuário logado
                lista = tarefaDAO.findByResponsavel(SessionManager.getUsuarioLogado().getId());
            } else {
                // Administrador e gerente veem todas
                lista = tarefaDAO.findAll();
            }

            tarefas.clear();
            tarefas.addAll(lista);
        } catch (Exception e) {
            e.printStackTrace();
            MainApp.showError("Erro", "Erro ao carregar tarefas: " + e.getMessage());
        }
    }

    @FXML
    private void handleNova() {
        openTarefaForm(null);
    }

    @FXML
    private void handleRefresh() {
        loadTarefas();
    }

    /**
     * Edita uma tarefa
     */
    private void handleEditar(Tarefa tarefa) {
        openTarefaForm(tarefa);
    }

    /**
     * Altera status de uma tarefa
     */
    private void handleAlterarStatus(Tarefa tarefa) {
        try {
            // Cria dialog para seleção de status
            ChoiceDialog<Tarefa.Status> dialog = new ChoiceDialog<>(tarefa.getStatus(), Tarefa.Status.values());
            dialog.setTitle("Alterar Status");
            dialog.setHeaderText("Alterar status da tarefa:");
            dialog.setContentText("Novo status:");

            dialog.showAndWait().ifPresent(novoStatus -> {
                try {
                    tarefaDAO.updateStatus(tarefa.getId(), novoStatus);
                    loadTarefas();
                    MainApp.showInfo("Sucesso", "Status da tarefa atualizado.");
                } catch (Exception e) {
                    e.printStackTrace();
                    MainApp.showError("Erro", "Erro ao atualizar status: " + e.getMessage());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            MainApp.showError("Erro", "Erro ao alterar status: " + e.getMessage());
        }
    }

    /**
     * Abre formulário de tarefa
     */
    private void openTarefaForm(Tarefa tarefa) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TarefaForm.fxml"));
            Scene scene = new Scene(loader.load());

            TarefaFormController controller = loader.getController();
            if (tarefa != null) {
                controller.setTarefa(tarefa);
            }

            Stage stage = new Stage();
            stage.setTitle(tarefa == null ? "Nova Tarefa" : "Editar Tarefa");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(MainApp.getPrimaryStage());
            stage.setResizable(false);

            stage.setOnHidden(e -> loadTarefas());

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            MainApp.showError("Erro", "Erro ao abrir formulário: " + e.getMessage());
        }
    }
}