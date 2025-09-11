package com.gestao.projetos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import com.gestao.projetos.MainApp;
import com.gestao.projetos.dao.UsuarioDAO;
import com.gestao.projetos.model.Usuario;
import com.gestao.projetos.util.SessionManager;

/**
 * Controller para a tela de login
 */
public class LoginController {

    @FXML
    private TextField txtLogin;
    @FXML
    private PasswordField txtSenha;
    @FXML
    private Button btnLogin;

    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    private void initialize() {
        // Permite login com Enter
        txtLogin.setOnKeyPressed(this::handleKeyPressed);
        txtSenha.setOnKeyPressed(this::handleKeyPressed);
    }

    @FXML
    private void handleLogin() {
        String login = txtLogin.getText().trim();
        String senha = txtSenha.getText();

        if (login.isEmpty()) {
            MainApp.showError("Erro", "Informe o login.");
            txtLogin.requestFocus();
            return;
        }

        if (senha.isEmpty()) {
            MainApp.showError("Erro", "Informe a senha.");
            txtSenha.requestFocus();
            return;
        }

        try {
            Usuario usuario = usuarioDAO.autenticar(login, senha);

            if (usuario != null) {
                SessionManager.setUsuarioLogado(usuario);
                MainApp.showMainMenu();
            } else {
                MainApp.showError("Erro", "Login ou senha inválidos.");
                txtSenha.clear();
                txtLogin.requestFocus();
            }

        } catch (Exception e) {
            e.printStackTrace();
            MainApp.showError("Erro", "Erro ao tentar fazer login: " + e.getMessage());
        }
    }

    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        }
    }
}