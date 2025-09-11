package com.gestao.projetos.dao;

import com.gestao.projetos.model.Projeto;
import com.gestao.projetos.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operações com projetos
 */
public class ProjetoDAO {

    /**
     * Busca todos os projetos ativos
     */
    public List<Projeto> findAll() throws SQLException {
        List<Projeto> projetos = new ArrayList<>();
        String sql = """
                SELECT p.*, u.nome as gerente_nome
                FROM projetos p
                INNER JOIN usuarios u ON p.gerente_id = u.id
                WHERE p.ativo = TRUE
                ORDER BY p.nome
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                projetos.add(resultSetToProjeto(rs));
            }
        }
        return projetos;
    }

    /**
     * Busca projetos por gerente
     */
    public List<Projeto> findByGerente(Integer gerenteId) throws SQLException {
        List<Projeto> projetos = new ArrayList<>();
        String sql = """
                SELECT p.*, u.nome as gerente_nome
                FROM projetos p
                INNER JOIN usuarios u ON p.gerente_id = u.id
                WHERE p.gerente_id = ? AND p.ativo = TRUE
                ORDER BY p.nome
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gerenteId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                projetos.add(resultSetToProjeto(rs));
            }
        }
        return projetos;
    }

    /**
     * Busca projeto por ID
     */
    public Projeto findById(Integer id) throws SQLException {
        String sql = """
                SELECT p.*, u.nome as gerente_nome
                FROM projetos p
                INNER JOIN usuarios u ON p.gerente_id = u.id
                WHERE p.id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return resultSetToProjeto(rs);
            }
        }
        return null;
    }

    /**
     * Busca projetos com resumo para relatórios
     */
    public List<Projeto> findProjetosResumo() throws SQLException {
        List<Projeto> projetos = new ArrayList<>();
        String sql = "SELECT * FROM vw_projetos_resumo ORDER BY nome";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Projeto projeto = new Projeto();
                projeto.setId(rs.getInt("id"));
                projeto.setNome(rs.getString("nome"));
                projeto.setStatus(Projeto.Status.valueOf(rs.getString("status")));
                projeto.setGerenteNome(rs.getString("gerente_nome"));
                projeto.setTotalTarefas(rs.getInt("total_tarefas"));
                projeto.setTarefasConcluidas(rs.getInt("tarefas_concluidas"));
                projeto.setPercentualConclusao(rs.getDouble("percentual_conclusao"));
                projetos.add(projeto);
            }
        }
        return projetos;
    }

    /**
     * Salva um novo projeto
     */
    public void save(Projeto projeto) throws SQLException {
        String sql = """
                INSERT INTO projetos (nome, descricao, data_inicio, data_prevista_termino,
                                     status, gerente_id)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, projeto.getNome());
            stmt.setString(2, projeto.getDescricao());
            stmt.setDate(3, Date.valueOf(projeto.getDataInicio()));
            stmt.setDate(4, Date.valueOf(projeto.getDataPrevistaTermino()));
            stmt.setString(5, projeto.getStatus().name());
            stmt.setInt(6, projeto.getGerenteId());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                projeto.setId(rs.getInt(1));
            }
        }
    }

    /**
     * Atualiza um projeto existente
     */
    public void update(Projeto projeto) throws SQLException {
        String sql = """
                UPDATE projetos SET
                    nome = ?, descricao = ?, data_inicio = ?, data_prevista_termino = ?,
                    status = ?, gerente_id = ?, data_atualizacao = CURRENT_TIMESTAMP
                WHERE id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, projeto.getNome());
            stmt.setString(2, projeto.getDescricao());
            stmt.setDate(3, Date.valueOf(projeto.getDataInicio()));
            stmt.setDate(4, Date.valueOf(projeto.getDataPrevistaTermino()));
            stmt.setString(5, projeto.getStatus().name());
            stmt.setInt(6, projeto.getGerenteId());
            stmt.setInt(7, projeto.getId());

            stmt.executeUpdate();
        }
    }

    /**
     * Cancela um projeto e suas tarefas
     */
    public void cancelProject(Integer projetoId) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);

        try {
            // Cancela o projeto
            String sqlProjeto = """
                    UPDATE projetos SET
                        status = 'CANCELADO',
                        data_atualizacao = CURRENT_TIMESTAMP
                    WHERE id = ?
                    """;
            PreparedStatement stmtProjeto = conn.prepareStatement(sqlProjeto);
            stmtProjeto.setInt(1, projetoId);
            stmtProjeto.executeUpdate();

            // Inativa todas as tarefas do projeto
            String sqlTarefas = """
                    UPDATE tarefas SET
                        ativo = FALSE,
                        data_atualizacao = CURRENT_TIMESTAMP
                    WHERE projeto_id = ? AND status NOT IN ('CONCLUIDA', 'CANCELADA')
                    """;
            PreparedStatement stmtTarefas = conn.prepareStatement(sqlTarefas);
            stmtTarefas.setInt(1, projetoId);
            stmtTarefas.executeUpdate();

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    /**
     * Converte ResultSet para objeto Projeto
     */
    private Projeto resultSetToProjeto(ResultSet rs) throws SQLException {
        Projeto projeto = new Projeto();
        projeto.setId(rs.getInt("id"));
        projeto.setNome(rs.getString("nome"));
        projeto.setDescricao(rs.getString("descricao"));

        Date dataInicio = rs.getDate("data_inicio");
        if (dataInicio != null) {
            projeto.setDataInicio(dataInicio.toLocalDate());
        }

        Date dataPrevistaTermino = rs.getDate("data_prevista_termino");
        if (dataPrevistaTermino != null) {
            projeto.setDataPrevistaTermino(dataPrevistaTermino.toLocalDate());
        }

        Date dataTerminoReal = rs.getDate("data_termino_real");
        if (dataTerminoReal != null) {
            projeto.setDataTerminoReal(dataTerminoReal.toLocalDate());
        }

        projeto.setStatus(Projeto.Status.valueOf(rs.getString("status")));
        projeto.setGerenteId(rs.getInt("gerente_id"));
        projeto.setGerenteNome(rs.getString("gerente_nome"));
        projeto.setAtivo(rs.getBoolean("ativo"));

        Timestamp dataCadastro = rs.getTimestamp("data_cadastro");
        if (dataCadastro != null) {
            projeto.setDataCadastro(dataCadastro.toLocalDateTime());
        }

        Timestamp dataAtualizacao = rs.getTimestamp("data_atualizacao");
        if (dataAtualizacao != null) {
            projeto.setDataAtualizacao(dataAtualizacao.toLocalDateTime());
        }

        return projeto;
    }
}