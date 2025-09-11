package com.gestao.projetos.dao;

import com.gestao.projetos.model.Usuario;
import com.gestao.projetos.util.DatabaseConnection;
import com.gestao.projetos.util.CryptUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operações com usuários
 */
public class UsuarioDAO {

    /**
     * Autentica um usuário no sistema
     */
    public Usuario autenticar(String login, String senha) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE login = ? AND ativo = TRUE";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String senhaHash = rs.getString("senha");

                // Para desenvolvimento, compara senha simples
                // Em produção, use: CryptUtils.verifyPassword(senha, senhaHash)
                if (senha.equals(senhaHash)) {
                    return resultSetToUsuario(rs);
                }
            }
        }
        return null;
    }

    /**
     * Busca todos os usuários ativos
     */
    public List<Usuario> findAll() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE ativo = TRUE ORDER BY nome";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(resultSetToUsuario(rs));
            }
        }
        return usuarios;
    }

    /**
     * Busca usuários por perfil
     */
    public List<Usuario> findByPerfil(Usuario.Perfil perfil) throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE perfil = ? AND ativo = TRUE ORDER BY nome";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, perfil.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                usuarios.add(resultSetToUsuario(rs));
            }
        }
        return usuarios;
    }

    /**
     * Busca usuário por ID
     */
    public Usuario findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return resultSetToUsuario(rs);
            }
        }
        return null;
    }

    /**
     * Salva um novo usuário
     */
    public void save(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, email, login, senha, perfil) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getLogin());
            // Em produção, use: CryptUtils.hashPassword(usuario.getSenha())
            stmt.setString(4, usuario.getSenha());
            stmt.setString(5, usuario.getPerfil().name());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                usuario.setId(rs.getInt(1));
            }
        }
    }

    /**
     * Atualiza um usuário existente
     */
    public void update(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuarios SET nome = ?, email = ?, login = ?, perfil = ?, " +
                "data_atualizacao = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getLogin());
            stmt.setString(4, usuario.getPerfil().name());
            stmt.setInt(5, usuario.getId());

            stmt.executeUpdate();
        }
    }

    /**
     * Atualiza senha do usuário
     */
    public void updatePassword(Integer userId, String novaSenha) throws SQLException {
        String sql = "UPDATE usuarios SET senha = ?, data_atualizacao = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Em produção, use: CryptUtils.hashPassword(novaSenha)
            stmt.setString(1, novaSenha);
            stmt.setInt(2, userId);

            stmt.executeUpdate();
        }
    }

    /**
     * Desativa um usuário (exclusão lógica)
     */
    public void deactivate(Integer id) throws SQLException {
        String sql = "UPDATE usuarios SET ativo = FALSE, data_atualizacao = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Verifica se login já existe
     */
    public boolean existsLogin(String login, Integer excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE login = ? AND ativo = TRUE";
        if (excludeId != null) {
            sql += " AND id != ?";
        }

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            if (excludeId != null) {
                stmt.setInt(2, excludeId);
            }

            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    /**
     * Verifica se email já existe
     */
    public boolean existsEmail(String email, Integer excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email = ? AND ativo = TRUE";
        if (excludeId != null) {
            sql += " AND id != ?";
        }

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            if (excludeId != null) {
                stmt.setInt(2, excludeId);
            }

            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        }
    }

    /**
     * Converte ResultSet para objeto Usuario
     */
    private Usuario resultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setEmail(rs.getString("email"));
        usuario.setLogin(rs.getString("login"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setPerfil(Usuario.Perfil.valueOf(rs.getString("perfil")));
        usuario.setAtivo(rs.getBoolean("ativo"));

        Timestamp dataCadastro = rs.getTimestamp("data_cadastro");
        if (dataCadastro != null) {
            usuario.setDataCadastro(dataCadastro.toLocalDateTime());
        }

        Timestamp dataAtualizacao = rs.getTimestamp("data_atualizacao");
        if (dataAtualizacao != null) {
            usuario.setDataAtualizacao(dataAtualizacao.toLocalDateTime());
        }

        return usuario;
    }
}
