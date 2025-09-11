package com.gestao.projetos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import com.gestao.projetos.MainApp;
import com.gestao.projetos.dao.UsuarioDAO;
import com.gestao.projetos.util.SessionManager;
import com.gestao.projetos.util.ValidationUtils;

/**
 * Controller para alterar senha do usuário logado
 */
public class AlterarSenhaController {
    
    @FXML private PasswordField txtSenhaAtual;
    @FXML private PasswordField txtNovaSenha;
    @FXML private PasswordField txtConfirmarSenha;
    @FXML private Button btnSalvar;
    
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    
    @FXML
    private void handleSalvar() {
        if (!validarCampos()) {
            return;
        }
        
        try {
            // Verifica senha atual
            String senhaAtual = txtSenhaAtual.getText();
            String loginUsuario = SessionManager.getUsuarioLogado().getLogin();
            
            if (usuarioDAO.autenticar(loginUsuario, senhaAtual) == null) {
                MainApp.showError("Erro", "Senha atual incorreta.");
                txtSenhaAtual.requestFocus();
                return;
            }
            
            // Atualiza senha
            String novaSenha = txtNovaSenha.getText();
            usuarioDAO.updatePassword(SessionManager.getUsuarioLogado().getId(), novaSenha);
            
            MainApp.showInfo("Sucesso", "Senha alterada com sucesso.");
            
            // Fecha janela
            Stage stage = (Stage) btnSalvar.getScene().getWindow();
            stage.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            MainApp.showError("Erro", "Erro ao alterar senha: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCancelar() {
        Stage stage = (Stage) btnSalvar.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Valida os campos
     */
    private boolean validarCampos() {
        if (!ValidationUtils.isNotEmpty(txtSenhaAtual, "Senha Atual")) return false;
        if (!ValidationUtils.isValidPassword(txtNovaSenha, 6)) return false;
        if (!ValidationUtils.isNotEmpty(txtConfirmarSenha, "Confirmar Senha")) return false;
        
        // Verifica se senhas coincidem
        if (!txtNovaSenha.getText().equals(txtConfirmarSenha.getText())) {
            MainApp.showError("Erro", "Nova senha e confirmação não coincidem.");
            txtConfirmarSenha.requestFocus();
            return false;
        }
        
        return true;
    }
}