package com.gestao.projetos.dao;

import com.gestao.projetos.model.Tarefa;
import com.gestao.projetos.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operações com tarefas
 */
public class TarefaDAO {

    /**
     * Busca todas as tarefas ativas
     */
    public List<Tarefa> findAll() throws SQLException {
        List<Tarefa> tarefas = new ArrayList<>();
        String sql = """
                SELECT t.*, p.nome as projeto_nome, u.nome as responsavel_nome
                FROM tarefas t
                INNER JOIN projetos p ON t.projeto_id = p.id
                INNER JOIN usuarios u ON t.responsavel_id = u.id
                WHERE t.ativo = TRUE
                ORDER BY t.titulo
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tarefas.add(resultSetToTarefa(rs));
            }
        }
        return tarefas;
    }

    /**
     * Busca tarefas por projeto
     */
    public List<Tarefa> findByProjeto(Integer projetoId) throws SQLException {
        List<Tarefa> tarefas = new ArrayList<>();
        String sql = """
                SELECT t.*, p.nome as projeto_nome, u.nome as responsavel_nome
                FROM tarefas t
                INNER JOIN projetos p ON t.projeto_id = p.id
                INNER JOIN usuarios u ON t.responsavel_id = u.id
                WHERE t.projeto_id = ? AND t.ativo = TRUE
                ORDER BY t.titulo
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, projetoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tarefas.add(resultSetToTarefa(rs));
            }
        }
        return tarefas;
    }

    /**
     * Busca tarefas por responsável
     */
    public List<Tarefa> findByResponsavel(Integer responsavelId) throws SQLException {
        List<Tarefa> tarefas = new ArrayList<>();
        String sql = """
                SELECT t.*, p.nome as projeto_nome, u.nome as responsavel_nome
                FROM tarefas t
                INNER JOIN projetos p ON t.projeto_id = p.id
                INNER JOIN usuarios u ON t.responsavel_id = u.id
                WHERE t.responsavel_id = ? AND t.ativo = TRUE
                ORDER BY t.titulo
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, responsavelId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tarefas.add(resultSetToTarefa(rs));
            }
        }
        return tarefas;
    }

    /**
     * Busca tarefa por ID
     */
    public Tarefa findById(Integer id) throws SQLException {
        String sql = """
                SELECT t.*, p.nome as projeto_nome, u.nome as responsavel_nome
                FROM tarefas t
                INNER JOIN projetos p ON t.projeto_id = p.id
                INNER JOIN usuarios u ON t.responsavel_id = u.id
                WHERE t.id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return resultSetToTarefa(rs);
            }
        }
        return null;
    }

    /**
     * Salva uma nova tarefa
     */
    public void save(Tarefa tarefa) throws SQLException {
        String sql = """
                INSERT INTO tarefas (titulo, descricao, projeto_id, responsavel_id,
                                   status, prioridade, data_prevista_conclusao)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, tarefa.getTitulo());
            stmt.setString(2, tarefa.getDescricao());
            stmt.setInt(3, tarefa.getProjetoId());
            stmt.setInt(4, tarefa.getResponsavelId());
            stmt.setString(5, tarefa.getStatus().name());
            stmt.setString(6, tarefa.getPrioridade().name());

            if (tarefa.getDataPrevistaConclusao() != null) {
                stmt.setDate(7, Date.valueOf(tarefa.getDataPrevistaConclusao()));
            } else {
                stmt.setNull(7, Types.DATE);
            }

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                tarefa.setId(rs.getInt(1));
            }
        }
    }

    /**
     * Atualiza uma tarefa existente
     */
    public void update(Tarefa tarefa) throws SQLException {
        String sql = """
                UPDATE tarefas SET
                    titulo = ?, descricao = ?, projeto_id = ?, responsavel_id = ?,
                    status = ?, prioridade = ?, data_prevista_conclusao = ?,
                    data_atualizacao = CURRENT_TIMESTAMP
                WHERE id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tarefa.getTitulo());
            stmt.setString(2, tarefa.getDescricao());
            stmt.setInt(3, tarefa.getProjetoId());
            stmt.setInt(4, tarefa.getResponsavelId());
            stmt.setString(5, tarefa.getStatus().name());
            stmt.setString(6, tarefa.getPrioridade().name());

            if (tarefa.getDataPrevistaConclusao() != null) {
                stmt.setDate(7, Date.valueOf(tarefa.getDataPrevistaConclusao()));
            } else {
                stmt.setNull(7, Types.DATE);
            }

            stmt.setInt(8, tarefa.getId());

            stmt.executeUpdate();
        }
    }

    /**
     * Atualiza apenas o status de uma tarefa
     */
    public void updateStatus(Integer tarefaId, Tarefa.Status novoStatus) throws SQLException {
        String sql = """
                UPDATE tarefas SET
                    status = ?,
                    data_conclusao_real = ?,
                    data_atualizacao = CURRENT_TIMESTAMP
                WHERE id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, novoStatus.name());

            // Se status for CONCLUIDA, define data de conclusão
            if (novoStatus == Tarefa.Status.CONCLUIDA) {
                stmt.setDate(2, Date.valueOf(LocalDate.now()));
            } else {
                stmt.setNull(2, Types.DATE);
            }

            stmt.setInt(3, tarefaId);

            stmt.executeUpdate();
        }
    }

    /**
     * Desativa uma tarefa (exclusão lógica)
     */
    public void deactivate(Integer id) throws SQLException {
        String sql = "UPDATE tarefas SET ativo = FALSE, data_atualizacao = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Busca estatísticas de tarefas por usuário
     */
    public List<Object[]> getTarefasPorUsuario() throws SQLException {
        List<Object[]> resultado = new ArrayList<>();
        String sql = "SELECT * FROM vw_tarefas_por_usuario ORDER BY usuario_nome";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object[] linha = {
                        rs.getString("usuario_nome"),
                        rs.getString("perfil"),
                        rs.getInt("total_tarefas"),
                        rs.getInt("tarefas_pendentes"),
                        rs.getInt("tarefas_em_execucao"),
                        rs.getInt("tarefas_concluidas")
                };
                resultado.add(linha);
            }
        }
        return resultado;
    }

    /**
     * Verifica se usuário pode editar a tarefa
     */
    public boolean canUserEditTask(Integer tarefaId, Integer userId, boolean isAdmin, boolean isGerente)
            throws SQLException {
        if (isAdmin)
            return true;

        String sql = """
                SELECT t.responsavel_id, p.gerente_id
                FROM tarefas t
                INNER JOIN projetos p ON t.projeto_id = p.id
                WHERE t.id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, tarefaId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Integer responsavelId = rs.getInt("responsavel_id");
                Integer gerenteId = rs.getInt("gerente_id");

                // Responsável pode editar status da tarefa
                if (userId.equals(responsavelId))
                    return true;

                // Gerente do projeto pode editar tudo
                if (isGerente && userId.equals(gerenteId))
                    return true;
            }
        }
        return false;
    }

    /**
     * Converte ResultSet para objeto Tarefa
     */
    private Tarefa resultSetToTarefa(ResultSet rs) throws SQLException {
        Tarefa tarefa = new Tarefa();
        tarefa.setId(rs.getInt("id"));
        tarefa.setTitulo(rs.getString("titulo"));
        tarefa.setDescricao(rs.getString("descricao"));
        tarefa.setProjetoId(rs.getInt("projeto_id"));
        tarefa.setProjetoNome(rs.getString("projeto_nome"));
        tarefa.setResponsavelId(rs.getInt("responsavel_id"));
        tarefa.setResponsavelNome(rs.getString("responsavel_nome"));
        tarefa.setStatus(Tarefa.Status.valueOf(rs.getString("status")));
        tarefa.setPrioridade(Tarefa.Prioridade.valueOf(rs.getString("prioridade")));

        Date dataPrevistaConclusao = rs.getDate("data_prevista_conclusao");
        if (dataPrevistaConclusao != null) {
            tarefa.setDataPrevistaConclusao(dataPrevistaConclusao.toLocalDate());
        }

        Date dataConclusaoReal = rs.getDate("data_conclusao_real");
        if (dataConclusaoReal != null) {
            tarefa.setDataConclusaoReal(dataConclusaoReal.toLocalDate());
        }

        tarefa.setAtivo(rs.getBoolean("ativo"));

        Timestamp dataCadastro = rs.getTimestamp("data_cadastro");
        if (dataCadastro != null) {
            tarefa.setDataCadastro(dataCadastro.toLocalDateTime());
        }

        Timestamp dataAtualizacao = rs.getTimestamp("data_atualizacao");
        if (dataAtualizacao != null) {
            tarefa.setDataAtualizacao(dataAtualizacao.toLocalDateTime());
        }

        return tarefa;
    }
}