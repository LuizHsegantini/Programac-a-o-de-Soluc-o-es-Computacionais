package com.gestao.projetos.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;

import com.gestao.projetos.MainApp;
import com.gestao.projetos.dao.ProjetoDAO;
import com.gestao.projetos.dao.UsuarioDAO;
import com.gestao.projetos.model.Projeto;
import com.gestao.projetos.model.Usuario;
import com.gestao.projetos.util.SessionManager;
import com.gestao.projetos.util.ValidationUtils;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller para formulário de projeto
 */
public class ProjetoFormController {

    @FXML
    private Label lblTitle;
    @FXML
    private TextField txtNome;
    @FXML
    private TextArea txtDescricao;
    @FXML
    private DatePicker dpDataInicio;
    @FXML
    private DatePicker dpDataPrevista;
    @FXML
    private ComboBox<Projeto.Status> cbStatus;
    @FXML
    private ComboBox<Usuario> cbGerente;
    @FXML
    private Button btnSalvar;

    private ProjetoDAO projetoDAO = new ProjetoDAO();
    private UsuarioDAO usuarioDAO = new UsuarioDAO();
    private Projeto projeto; // null = novo projeto

    @FXML
    private void initialize() {
        setupComboBoxes();

        // Define data inicial padrão
        dpDataInicio.setValue(LocalDate.now());
    }

    /**
     * Configura ComboBoxes
     */
    private void setupComboBoxes() {
        // Status
        cbStatus.setItems(FXCollections.observableArrayList(Projeto.Status.values()));
        cbStatus.setValue(Projeto.Status.PLANEJADO);

        // Gerentes (apenas usuários com perfil gerente ou administrador)
        try {
            List<Usuario> gerentes = usuarioDAO.findByPerfil(Usuario.Perfil.GERENTE);
            List<Usuario> admins = usuarioDAO.findByPerfil(Usuario.Perfil.ADMINISTRADOR);

            gerentes.addAll(admins);
            cbGerente.setItems(FXCollections.observableArrayList(gerentes));

            // Se usuário logado é gerente, seleciona automaticamente
            if (SessionManager.isGerente() && !SessionManager.isAdministrador()) {
                cbGerente.setValue(SessionManager.getUsuarioLogado());
                cbGerente.setDisable(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            MainApp.showError("Erro", "Erro ao carregar gerentes: " + e.getMessage());
        }
    }

    /**
     * Define projeto para edição
     */
    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
        lblTitle.setText("Editar Projeto");
        btnSalvar.setText("Atualizar");

        // Preenche campos
        txtNome.setText(projeto.getNome());
        txtDescricao.setText(projeto.getDescricao());
        dpDataInicio.setValue(projeto.getDataInicio());
        dpDataPrevista.setValue(projeto.getDataPrevistaTermino());
        cbStatus.setValue(projeto.getStatus());

        // Seleciona gerente
        try {
            Usuario gerente = usuarioDAO.findById(projeto.getGerenteId());
            cbGerente.setValue(gerente);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSalvar() {
        if (!validarCampos()) {
            return;
        }

        try {
            if (projeto == null) {
                criarNovoProjeto();
            } else {
                atualizarProjeto();
            }

            Stage stage = (Stage) btnSalvar.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            e.printStackTrace();
            MainApp.showError("Erro", "Erro ao salvar projeto: " + e.getMessage());
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
        if (!ValidationUtils.isNotEmpty(txtNome, "Nome"))
            return false;
        if (!ValidationUtils.isNotEmpty(txtDescricao, "Descrição"))
            return false;
        if (!ValidationUtils.hasDate(dpDataInicio, "Data de Início"))
            return false;
        if (!ValidationUtils.hasDate(dpDataPrevista, "Data Prevista"))
            return false;
        if (!ValidationUtils.hasSelection(cbStatus, "Status"))
            return false;
        if (!ValidationUtils.hasSelection(cbGerente, "Gerente"))
            return false;

        // Valida datas
        if (dpDataPrevista.getValue().isBefore(dpDataInicio.getValue())) {
            MainApp.showError("Erro", "Data prevista deve ser posterior à data de início.");
            dpDataPrevista.requestFocus();
            return false;
        }

        return true;
    }

    /**
     * Cria novo projeto
     */
    private void criarNovoProjeto() throws Exception {
        Projeto novoProjeto = new Projeto();
        novoProjeto.setNome(txtNome.getText().trim());
        novoProjeto.setDescricao(txtDescricao.getText().trim());
        novoProjeto.setDataInicio(dpDataInicio.getValue());
        novoProjeto.setDataPrevistaTermino(dpDataPrevista.getValue());
        novoProjeto.setStatus(cbStatus.getValue());
        novoProjeto.setGerenteId(cbGerente.getValue().getId());

        projetoDAO.save(novoProjeto);
        MainApp.showInfo("Sucesso", "Projeto cadastrado com sucesso.");
    }

    /**
     * Atualiza projeto existente
     */
    private void atualizarProjeto() throws Exception {
        projeto.setNome(txtNome.getText().trim());
        projeto.setDescricao(txtDescricao.getText().trim());
        projeto.setDataInicio(dpDataInicio.getValue());
        projeto.setDataPrevistaTermino(dpDataPrevista.getValue());
        projeto.setStatus(cbStatus.getValue());
        projeto.setGerenteId(cbGerente.getValue().getId());

        projetoDAO.update(projeto);
        MainApp.showInfo("Sucesso", "Projeto atualizado com sucesso.");
    }
}