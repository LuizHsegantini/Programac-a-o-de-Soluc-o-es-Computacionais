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
import com.gestao.projetos.dao.UsuarioDAO;
import com.gestao.projetos.model.Usuario;
import com.gestao.projetos.util.SessionManager;

import java.util.List;

/**
 * Controller para listagem de usuários
 */
public class UsuarioListController {

    @FXML
    private TableView<Usuario> tblUsuarios;
    @FXML
    private TableColumn<Usuario, String> colNome;
    @FXML
    private TableColumn<Usuario, String> colEmail;
    @FXML
    private TableColumn<Usuario, String> colLogin;
    @FXML
    private TableColumn<Usuario, String> colPerfil;
    @FXML
    private TableColumn<Usuario, String> colStatus;
    @FXML
    private TableColumn<Usuario, Void> colAcoes;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private ObservableList<Usuario> usuarios = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        setupTable();
        loadUsuarios();
    }

    /**
     * Configura a tabela de usuários
     */
    private void setupTable() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colPerfil.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getPerfil().getDescricao()));
        colStatus.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().isAtivo() ? "Ativo" : "Inativo"));

        // Coluna de ações
        colAcoes.setCellFactory(param -> new TableCell<Usuario, Void>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnExcluir = new Button("Excluir");
            private final javafx.scene.layout.HBox pane = new javafx.scene.layout.HBox(5, btnEditar, btnExcluir);

            {
                btnEditar.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    handleEditar(usuario);
                });

                btnExcluir.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    handleExcluir(usuario);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        tblUsuarios.setItems(usuarios);
    }

    /**
     * Carrega lista de usuários
     */
    private void loadUsuarios() {
        try {
            List<Usuario> lista = usuarioDAO.findAll();
            usuarios.clear();
            usuarios.addAll(lista);
        } catch (Exception e) {
            e.printStackTrace();
            MainApp.showError("Erro", "Erro ao carregar usuários: " + e.getMessage());
        }
    }

    @FXML
    private void handleNovo() {
        openUsuarioForm(null);
    }

    @FXML
    private void handleRefresh() {
        loadUsuarios();
    }

    /**
     * Edita um usuário
     */
    private void handleEditar(Usuario usuario) {
        openUsuarioForm(usuario);
    }

    /**
     * Exclui um usuário
     */
    private void handleExcluir(Usuario usuario) {
        if (usuario.getId().equals(SessionManager.getUsuarioLogado().getId())) {
            MainApp.showError("Erro", "Você não pode excluir seu próprio usuário.");
            return;
        }

        if (MainApp.showConfirmation("Confirmação",
                "Tem certeza que deseja excluir o usuário '" + usuario.getNome() + "'?")) {
            try {
                usuarioDAO.deactivate(usuario.getId());
                loadUsuarios();
                MainApp.showInfo("Sucesso", "Usuário excluído com sucesso.");
            } catch (Exception e) {
                e.printStackTrace();
                MainApp.showError("Erro", "Erro ao excluir usuário: " + e.getMessage());
            }
        }
    }

    /**
     * Abre formulário de usuário
     */
    private void openUsuarioForm(Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UsuarioForm.fxml"));
            Scene scene = new Scene(loader.load());

            UsuarioFormController controller = loader.getController();
            if (usuario != null) {
                controller.setUsuario(usuario);
            }

            Stage stage = new Stage();
            stage.setTitle(usuario == null ? "Novo Usuário" : "Editar Usuário");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(MainApp.getPrimaryStage());
            stage.setResizable(false);

            stage.setOnHidden(e -> loadUsuarios()); // Atualiza lista após fechar

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            MainApp.showError("Erro", "Erro ao abrir formulário: " + e.getMessage());
        }
    }
}
