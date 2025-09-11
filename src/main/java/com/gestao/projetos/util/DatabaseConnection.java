package com.gestao.projetos.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilitária para gerenciar conexões com o banco de dados MySQL
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/gestao_projetos?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Configure sua senha do MySQL aqui

    private static Connection connection = null;

    /**
     * Obtem uma conexão com o banco de dados
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver MySQL não encontrado", e);
            }
        }
        return connection;
    }

    /**
     * Testa a conexão com o banco de dados
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Erro ao testar conexão: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fecha a conexão com o banco de dados
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
}