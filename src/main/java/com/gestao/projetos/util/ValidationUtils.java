package com.gestao.projetos.util;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import java.util.regex.Pattern;

/**
 * Classe utilitária para validações
 */
public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    /**
     * Valida se um campo de texto não está vazio
     */
    public static boolean isNotEmpty(TextField field, String fieldName) {
        if (field.getText() == null || field.getText().trim().isEmpty()) {
            showValidationError("O campo '" + fieldName + "' é obrigatório.");
            field.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Valida se uma área de texto não está vazia
     */
    public static boolean isNotEmpty(TextArea field, String fieldName) {
        if (field.getText() == null || field.getText().trim().isEmpty()) {
            showValidationError("O campo '" + fieldName + "' é obrigatório.");
            field.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Valida se um ComboBox tem um item selecionado
     */
    public static boolean hasSelection(ComboBox<?> combo, String fieldName) {
        if (combo.getSelectionModel().getSelectedItem() == null) {
            showValidationError("Selecione um item para '" + fieldName + "'.");
            combo.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Valida se um DatePicker tem uma data selecionada
     */
    public static boolean hasDate(DatePicker datePicker, String fieldName) {
        if (datePicker.getValue() == null) {
            showValidationError("Selecione uma data para '" + fieldName + "'.");
            datePicker.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Valida formato de email
     */
    public static boolean isValidEmail(TextField field) {
        String email = field.getText().trim();
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showValidationError("Email inválido.");
            field.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Valida tamanho mínimo de senha
     */
    public static boolean isValidPassword(TextField field, int minLength) {
        String password = field.getText();
        if (password.length() < minLength) {
            showValidationError("A senha deve ter pelo menos " + minLength + " caracteres.");
            field.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * Exibe mensagem de erro de validação
     */
    private static void showValidationError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validação");
        alert.setHeaderText("Dados inválidos");
        alert.setContentText(message);
        alert.showAndWait();
    }
}