package com.gestao.projetos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import com.gestao.projetos.util.DatabaseConnection;
import com.gestao.projetos.util.SessionManager;

/**
 * Classe principal da aplicação Sistema de Gestão de Projetos e Equipes
 * 
 * @author Sistema
 * @version 1.0
 */
public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        // Teste de conexão com banco de dados
        if (!DatabaseConnection.testConnection()) {
            showError("Erro de Conexão",
                    "Não foi possível conectar ao banco de dados.\n" +
                            "Verifique se o MySQL está rodando e as configurações estão corretas.");
            return;
        }

        showLoginScreen();
    }

    /**
     * Exibe a tela de login
     */
    public static void showLoginScreen() {
        try {
            // Limpa a sessão atual
            SessionManager.clearSession();

            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/Login.fxml"));
            Scene scene = new Scene(loader.load());

            primaryStage.setTitle("Sistema de Gestão de Projetos - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erro", "Erro ao carregar tela de login: " + e.getMessage());
        }
    }

    /**
     * Exibe o menu principal após login bem-sucedido
     */
    public static void showMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("/fxml/MainMenu.fxml"));
            Scene scene = new Scene(loader.load());

            primaryStage.setTitle("Sistema de Gestão de Projetos - Menu Principal");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setMaximized(true);
            primaryStage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erro", "Erro ao carregar menu principal: " + e.getMessage());
        }
    }

    /**
     * Exibe dialog de erro
     */
    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Exibe dialog de informação
     */
    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Exibe dialog de confirmação
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        return alert.showAndWait().get() == javafx.scene.control.ButtonType.OK;
    }

    /**
     * Retorna o stage principal
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}