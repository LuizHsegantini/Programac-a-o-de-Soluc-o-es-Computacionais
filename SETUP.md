# 🚀 Setup do Sistema de Gestão de Projetos

## ⚡ Setup Automático (Recomendado)

```bash
# Execute este comando para setup completo:
python setup_completo.py
```

## 📋 O que o setup automático faz:

1. ✅ **Organiza arquivos** na estrutura correta
2. ✅ **Verifica dependências** (Java, Maven, MySQL)
3. ✅ **Instala dependências** faltantes (portável)
4. ✅ **Baixa MySQL Connector**
5. ✅ **Cria arquivos** faltantes
6. ✅ **Valida estrutura** do projeto
7. ✅ **Gera relatórios** de status

## 🔧 Setup Manual

### 1. Pré-requisitos
- **Java 11+**: https://adoptium.net/
- **Maven 3.6+**: https://maven.apache.org/download.cgi
- **MySQL 8.0+**: https://dev.mysql.com/downloads/

### 2. Configuração do Banco
```sql
-- Execute no MySQL:
CREATE DATABASE gestao_projetos;
SOURCE database/database_script.sql;
```

### 3. Configuração do Projeto
```bash
# Ajuste credenciais em:
src/main/java/com/gestao/projetos/util/DatabaseConnection.java

# Compile o projeto:
mvn clean compile

# Execute o sistema:
mvn javafx:run
```

## 🎯 Scripts Disponíveis

- `python compile.py` - Compila o projeto
- `python run.py` - Executa o sistema  
- `python reset.py` - Limpa arquivos temporários
- `setup_env.bat` - Configura ambiente (Windows)
- `setup_env.sh` - Configura ambiente (Linux/Mac)

## 👥 Usuários de Teste

- **admin / 123456** (Administrador)
- **joao / 123456** (Gerente)
- **pedro / 123456** (Colaborador)

## 🐛 Resolução de Problemas

### Erro de Compilação
```bash
# Limpa e recompila:
mvn clean compile
```

### Erro de Conexão com Banco
1. Verifique se MySQL está rodando
2. Confirme credenciais em DatabaseConnection.java
3. Teste conexão: `mysql -u root -p`

### Erro JavaFX
```bash
# Execute com módulos JavaFX:
mvn javafx:run
```

### Erro de Dependências
```bash
# Baixa dependências novamente:
mvn dependency:resolve
```

## 📞 Suporte

Se encontrar problemas:
1. Execute `python setup_completo.py` novamente
2. Verifique logs de erro
3. Consulte documentação completa em `docs/README.md`
