package com.gestao.projetos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;

import com.gestao.projetos.MainApp;
import com.gestao.projetos.dao.UsuarioDAO;
import com.gestao.projetos.model.Usuario;
import com.gestao.projetos.util.ValidationUtils;

/**
 * Controller para formulário de usuário
 */
public class UsuarioFormController {

    @FXML
    private Label lblTitle;
    @FXML
    private TextField txtNome;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtLogin;
    @FXML
    private PasswordField txtSenha;
    @FXML
    private ComboBox<Usuario.Perfil> cbPerfil;
    @FXML
    private Button btnSalvar;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private Usuario usuario; // null = novo usuário

    @FXML
    private void initialize() {
        // Configura ComboBox de perfis
        cbPerfil.setItems(FXCollections.observableArrayList(Usuario.Perfil.values()));
        cbPerfil.setValue(Usuario.Perfil.COLABORADOR);
    }

    /**
     * Define usuário para edição
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        lblTitle.setText("Editar Usuário");
        btnSalvar.setText("Atualizar");

        // Preenche campos
        txtNome.setText(usuario.getNome());
        txtEmail.setText(usuario.getEmail());
        txtLogin.setText(usuario.getLogin());
        txtSenha.setPromptText("Digite nova senha (deixe vazio para manter atual)");
        cbPerfil.setValue(usuario.getPerfil());
    }

    @FXML
    private void handleSalvar() {
        if (!validarCampos()) {
            return;
        }

        try {
            if (usuario == null) {
                // Novo usuário
                criarNovoUsuario();
            } else {
                // Atualizar usuário existente
                atualizarUsuario();
            }

            // Fecha janela
            Stage stage = (Stage) btnSalvar.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            MainApp.showError("Erro", "Erro ao salvar usuário: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) btnSalvar.getScene().getWindow();
        stage.close();
    }

    /**
     * Valida os campos do formulário
     */
    private boolean validarCampos() {
        // Validações básicas
        if (!ValidationUtils.isNotEmpty(txtNome, "Nome"))
            return false;
        if (!ValidationUtils.isNotEmpty(txtEmail, "Email"))
            return false;
        if (!ValidationUtils.isNotEmpty(txtLogin, "Login"))
            return false;
        if (!ValidationUtils.hasSelection(cbPerfil, "Perfil"))
            return false;

        // Validação de email
        if (!ValidationUtils.isValidEmail(txtEmail))
            return false;

        // Validação de senha (apenas para novo usuário)
        if (usuario == null && !ValidationUtils.isValidPassword(txtSenha, 6)) {
            return false;
        }

        try {
            // Verifica se login já existe
            Integer excludeId = usuario != null ? usuario.getId() : null;
            if (usuarioDAO.existsLogin(txtLogin.getText().trim(), excludeId)) {
                MainApp.showError("Erro", "Login já existe no sistema.");
                txtLogin.requestFocus();
                return false;
            }

            // Verifica se email já existe
            if (usuarioDAO.existsEmail(txtEmail.getText().trim(), excludeId)) {
                MainApp.showError("Erro", "Email já existe no sistema.");
                txtEmail.requestFocus();
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            MainApp.showError("Erro", "Erro ao validar dados: " + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Cria novo usuário
     */
    private void criarNovoUsuario() throws Exception {
        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(txtNome.getText().trim());
        novoUsuario.setEmail(txtEmail.getText().trim());
        novoUsuario.setLogin(txtLogin.getText().trim());
        novoUsuario.setSenha(txtSenha.getText()); // Em produção, use hash
        novoUsuario.setPerfil(cbPerfil.getValue());

        usuarioDAO.save(novoUsuario);
        MainApp.showInfo("Sucesso", "Usuário cadastrado com sucesso.");
    }

    /**
     * Atualiza usuário existente
     */
    private void atualizarUsuario() throws Exception {
        usuario.setNome(txtNome.getText().trim());
        usuario.setEmail(txtEmail.getText().trim());
        usuario.setLogin(txtLogin.getText().trim());
        usuario.setPerfil(cbPerfil.getValue());

        usuarioDAO.update(usuario);

        // Atualiza senha se informada
        String novaSenha = txtSenha.getText();
        if (!novaSenha.isEmpty()) {
            usuarioDAO.updatePassword(usuario.getId(), novaSenha);
        }

        MainApp.showInfo("Sucesso", "Usuário atualizado com sucesso.");
    }
}