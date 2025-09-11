# Sistema de Gestão de Projetos e Equipes

## 📋 Visão Geral

Sistema desenvolvido em **Java** com **JavaFX** e **MySQL** para gerenciamento de projetos, equipes e tarefas. O sistema implementa diferentes perfis de usuário com permissões específicas e oferece funcionalidades completas de CRUD, autenticação e relatórios.

## 🔧 Tecnologias Utilizadas

- **Java 11+** - Linguagem principal
- **JavaFX** - Interface gráfica
- **MySQL 8.0+** - Banco de dados
- **Arquitetura MVC** - Padrão de desenvolvimento

## 📁 Estrutura do Projeto

```
src/
├── com/gestao/projetos/
│   ├── MainApp.java                     # Classe principal
│   ├── model/                           # Modelos de dados
│   │   ├── Usuario.java
│   │   ├── Projeto.java
│   │   └── Tarefa.java
│   ├── dao/                             # Acesso aos dados
│   │   ├── UsuarioDAO.java
│   │   ├── ProjetoDAO.java
│   │   └── TarefaDAO.java
│   ├── controller/                      # Controllers das telas
│   │   ├── LoginController.java
│   │   ├── MainMenuController.java
│   │   ├── DashboardController.java
│   │   ├── UsuarioListController.java
│   │   ├── UsuarioFormController.java
│   │   ├── ProjetoListController.java
│   │   ├── ProjetoFormController.java
│   │   └── TarefaListController.java
│   └── util/                            # Utilitários
│       ├── DatabaseConnection.java
│       ├── SessionManager.java
│       ├── CryptUtils.java
│       └── ValidationUtils.java
└── resources/
    └── fxml/                            # Arquivos de interface
        ├── Login.fxml
        ├── MainMenu.fxml
        ├── Dashboard.fxml
        ├── UsuarioList.fxml
        ├── UsuarioForm.fxml
        ├── ProjetoList.fxml
        ├── ProjetoForm.fxml
        └── TarefaList.fxml
```

## 🚀 Configuração e Instalação

### 1. Pré-requisitos

- **Java JDK 11+**
- **MySQL 8.0+**
- **IDE** (IntelliJ IDEA, Eclipse, NetBeans)
- **MySQL Connector/J** (driver JDBC)

### 2. Configuração do Banco de Dados

1. Execute o script SQL fornecido no MySQL:
```sql
-- Executar o script database_script.sql
```

2. Configure as credenciais em `DatabaseConnection.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/gestao_projetos";
private static final String USER = "root";
private static final String PASSWORD = "sua_senha_aqui";
```

### 3. Dependências Maven

Adicione no `pom.xml`:

```xml
<dependencies>
    <!-- JavaFX -->
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>17.0.2</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>17.0.2</version>
    </dependency>
    
    <!-- MySQL Connector -->
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.33</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-maven-plugin</artifactId>
            <version>0.0.8</version>
            <configuration>
                <mainClass>com.gestao.projetos.MainApp</mainClass>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### 4. Executar o Sistema

```bash
# Via Maven
mvn javafx:run

# Via IDE
# Execute a classe MainApp.java
```

## 👥 Perfis de Usuário e Permissões

### 🔴 Administrador
- **Usuários**: Criar, editar, excluir usuários
- **Projetos**: Visualizar e gerenciar todos os projetos
- **Tarefas**: Visualizar e gerenciar todas as tarefas
- **Relatórios**: Acesso a todos os relatórios

### 🟡 Gerente
- **Usuários**: Apenas visualizar
- **Projetos**: Criar e gerenciar apenas seus projetos
- **Tarefas**: Criar e gerenciar tarefas dos seus projetos
- **Relatórios**: Relatórios dos seus projetos

### 🟢 Colaborador
- **Usuários**: Sem acesso
- **Projetos**: Visualizar projetos onde participa
- **Tarefas**: Visualizar e atualizar status das suas tarefas
- **Relatórios**: Sem acesso

## 🗄️ Modelo de Dados

### Tabela: usuarios
```sql
- id (PK)
- nome
- email (UNIQUE)
- login (UNIQUE)
- senha
- perfil (ADMINISTRADOR, GERENTE, COLABORADOR)
- ativo
- data_cadastro
- data_atualizacao
```

### Tabela: projetos
```sql
- id (PK)
- nome
- descricao
- data_inicio
- data_prevista_termino
- data_termino_real
- status (PLANEJADO, EM_ANDAMENTO, CONCLUIDO, CANCELADO)
- gerente_id (FK -> usuarios.id)
- ativo
- data_cadastro
- data_atualizacao
```

### Tabela: tarefas
```sql
- id (PK)
- titulo
- descricao
- projeto_id (FK -> projetos.id)
- responsavel_id (FK -> usuarios.id)
- status (PENDENTE, EM_EXECUCAO, CONCLUIDA, CANCELADA)
- prioridade (BAIXA, MEDIA, ALTA)
- data_prevista_conclusao
- data_conclusao_real
- ativo
- data_cadastro
- data_atualizacao
```

### Tabela: usuario_projeto
```sql
- id (PK)
- usuario_id (FK -> usuarios.id)
- projeto_id (FK -> projetos.id)
- data_vinculo
- ativo
```

## 🎯 Funcionalidades Principais

### 🔐 Autenticação
- Tela de login com validação no banco
- Gerenciamento de sessão
- Controle de permissões por perfil

### 👥 Gestão de Usuários
- Cadastro de usuários com perfis
- Edição de dados pessoais
- Alteração de senhas
- Desativação de usuários

### 📊 Gestão de Projetos
- Criação e edição de projetos
- Atribuição de gerentes
- Controle de status
- Cancelamento com inativação de tarefas

### ✅ Gestão de Tarefas
- Criação vinculada a projetos
- Atribuição de responsáveis
- Controle de status e prioridades
- Filtros por usuário e projeto

### 📈 Dashboard e Relatórios
- Visão geral com estatísticas
- Projetos e tarefas recentes
- Relatórios de progresso
- Métricas por usuário

## 🖥️ Telas do Sistema

### Login
- Campo de usuário e senha
- Validação no banco de dados
- Redirecionamento baseado em permissões

### Menu Principal
- Menu adaptativo por perfil
- Barra de ferramentas
- Área de conteúdo dinâmico

### Dashboard
- Cards com estatísticas gerais
- Tabelas de projetos recentes
- Tabelas de tarefas recentes
- Dados filtrados por perfil

### Usuários
- Lista com funcionalidades CRUD
- Formulário de cadastro/edição
- Validação de dados únicos (email/login)

### Projetos
- Lista com permissões por usuário
- Formulário com seleção de gerentes
- Cancelamento com confirmação

### Tarefas
- Lista geral e "Minhas Tarefas"
- Formulário com vinculação a projetos
- Alteração rápida de status

## 🔒 Regras de Negócio

### Usuários
- Login e email devem ser únicos
- Senha mínima de 6 caracteres
- Apenas administradores gerenciam usuários
- Usuário não pode excluir a si mesmo

### Projetos
- Apenas gerentes e administradores criam projetos
- Gerente só gerencia seus próprios projetos
- Data prevista deve ser posterior ao início
- Cancelamento inativa tarefas pendentes

### Tarefas
- Toda tarefa pertence a um projeto
- Responsável pode alterar status das suas tarefas
- Gerente do projeto pode editar todas as tarefas
- Tarefas de projetos cancelados ficam inativas

## 🛠️ Recursos Técnicos

### Arquitetura MVC
- **Model**: Classes de entidade com enums
- **View**: Arquivos FXML com JavaFX
- **Controller**: Lógica de apresentação e navegação
- **DAO**: Camada de acesso aos dados

### Utilitários
- **DatabaseConnection**: Pool de conexões
- **SessionManager**: Controle de usuário logado
- **ValidationUtils**: Validações reutilizáveis
- **CryptUtils**: Hash de senhas (SHA-256)

### Segurança
- Senhas com hash SHA-256
- Validação de permissões em todas as operações
- Proteção contra SQL Injection (PreparedStatement)
- Controle de sessão com timeout

## 📊 Views e Relatórios

### vw_projetos_resumo
```sql
SELECT 
    p.id, p.nome, p.status, u.nome as gerente_nome,
    COUNT(t.id) as total_tarefas,
    SUM(CASE WHEN t.status = 'CONCLUIDA' THEN 1 ELSE 0 END) as tarefas_concluidas,
    ROUND((SUM(CASE WHEN t.status = 'CONCLUIDA' THEN 1 ELSE 0 END) * 100.0) / 
          NULLIF(COUNT(t.id), 0), 2) as percentual_conclusao
FROM projetos p
LEFT JOIN usuarios u ON p.gerente_id = u.id
LEFT JOIN tarefas t ON p.id = t.projeto_id AND t.ativo = TRUE
WHERE p.ativo = TRUE
GROUP BY p.id, p.nome, p.status, u.nome;
```

### vw_tarefas_por_usuario
```sql
SELECT 
    u.id as usuario_id, u.nome as usuario_nome, u.perfil,
    COUNT(t.id) as total_tarefas,
    SUM(CASE WHEN t.status = 'PENDENTE' THEN 1 ELSE 0 END) as tarefas_pendentes,
    SUM(CASE WHEN t.status = 'EM_EXECUCAO' THEN 1 ELSE 0 END) as tarefas_em_execucao,
    SUM(CASE WHEN t.status = 'CONCLUIDA' THEN 1 ELSE 0 END) as tarefas_concluidas
FROM usuarios u
LEFT JOIN tarefas t ON u.id = t.responsavel_id AND t.ativo = TRUE
WHERE u.ativo = TRUE
GROUP BY u.id, u.nome, u.perfil;
```

## 🚀 Dados de Teste

### Usuários Padrão
- **admin/123456** - Administrador
- **joao/123456** - Gerente
- **maria/123456** - Gerente  
- **pedro/123456** - Colaborador
- **ana/123456** - Colaborador

### Projetos de Exemplo
- Sistema Web Vendas (Em Andamento)
- App Mobile Cliente (Planejado)
- Migração Banco Dados (Em Andamento)

### Tarefas de Exemplo
- Análise de Requisitos (Concluída)
- Design da Interface (Em Execução)
- Desenvolvimento Backend (Pendente)
- Testes Unitários (Pendente)

## 🔧 Melhorias Futuras

### Funcionalidades
- [ ] Notificações por email
- [ ] Calendário de tarefas
- [ ] Anexos em tarefas
- [ ] Chat entre equipes
- [ ] Timesheet de horas
- [ ] Kanban board

### Técnicas
- [ ] Cache de dados
- [ ] Logs de auditoria
- [ ] Backup automático
- [ ] API REST
- [ ] Testes unitários
- [ ] Deploy automatizado

## 📞 Suporte

Para dúvidas ou problemas:

1. Verifique a conexão com o banco de dados
2. Confirme as dependências do JavaFX
3. Valide as permissões do usuário MySQL
4. Consulte os logs de erro no console

## 📄 Licença

Este projeto foi desenvolvido para fins educacionais e pode ser usado livremente para aprendizado e desenvolvimento.