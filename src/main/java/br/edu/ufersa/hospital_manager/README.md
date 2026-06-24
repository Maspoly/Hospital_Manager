# 🏥 Hospital Manager
**Sistema de Gestão Hospitalar**

---

## 📋 Índice

1. [Introdução](#1-introdução)
2. [Primeiros Passos](#2-primeiros-passos)
3. [Telas do Sistema](#3-telas-do-sistema)
4. [Funcionalidades Gerais](#4-funcionalidades-gerais)
5. [Tutorial de Uso](#5-tutorial-de-uso)
6. [Estrutura de Dados](#6-estrutura-de-dados)
7. [Segurança](#7-segurança)
8. [Telas de Cadastro](#8-telas-de-cadastro)
9. [Atalhos do Teclado](#9-atalhos-do-teclado)
10. [Perguntas Frequentes (FAQ)](#10-perguntas-frequentes-faq)

---

## 1. Introdução

O **Hospital Manager** é um sistema de gestão para clínicas e hospitais que permite o gerenciamento de:

- 👨‍⚕️ Médicos
- 🧑‍🤝‍🧑 Pacientes
- 🏢 Gerentes
- 📅 Consultas
- 📋 Prontuários
- 📊 Relatórios

---

## 2. Primeiros Passos

### 2.1. Configuração do Banco de Dados

Antes de executar o programa, é necessário configurar o banco de dados MySQL.

**Passo 1:** Execute o script SQL abaixo para criar o banco de dados e as tabelas necessárias:

```sql
CREATE DATABASE IF NOT EXISTS hospital_manager;
USE hospital_manager;

-- =========================
-- ADDRESS
-- =========================
CREATE TABLE addresses (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    street VARCHAR(255) NOT NULL,
    number VARCHAR(20),
    neighborhood VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(100)
);

-- =========================
-- MANAGER
-- =========================
CREATE TABLE manager (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    address_id BIGINT,
    FOREIGN KEY (address_id) REFERENCES addresses(id)
);

-- =========================
-- DOCTOR
-- =========================
CREATE TABLE doctor (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    address_id BIGINT,
    consultation_value DECIMAL(10,2),
    council_code VARCHAR(50),
    FOREIGN KEY (address_id) REFERENCES addresses(id)
);

-- =========================
-- PATIENT
-- =========================
CREATE TABLE patient (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    address_id BIGINT,
    FOREIGN KEY (address_id) REFERENCES addresses(id)
);

-- =========================
-- CONSULTATION
-- =========================
CREATE TABLE consultation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    date_time DATETIME NOT NULL,
    status VARCHAR(50),
    FOREIGN KEY (patient_id) REFERENCES patient(id),
    FOREIGN KEY (doctor_id) REFERENCES doctor(id)
);

-- =========================
-- MEDICAL RECORD
-- =========================
CREATE TABLE medical_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    date DATE NOT NULL,
    observation TEXT,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    FOREIGN KEY (patient_id) REFERENCES patient(id),
    FOREIGN KEY (doctor_id) REFERENCES doctor(id)
);

-- =========================
-- DADOS INICIAIS
-- =========================

-- 1. Inserir o endereço do manager
INSERT INTO addresses (street, number, neighborhood, city, state)
VALUES ('Av. Central', '100', 'Centro', 'Mossoró', 'RN');

-- 2. Inserir o manager (senha: admin123)
INSERT INTO manager (name, cpf, password, address_id)
VALUES ('Administrador', '00000000000', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 1);
```

**Passo 2:** Verifique se a conexão com o banco está configurada corretamente no arquivo de configuração do projeto.

---

## 3. Telas do Sistema

### 3.1. Tela de Login

A tela de login é a porta de entrada do sistema.

**Campos:**
- **CPF:** Digite o CPF do usuário (apenas números, sem pontuação)
- **Senha:** Digite a senha do usuário

**Botões:**
- **Entrar:** Realiza a autenticação
- **Cadastrar novo paciente:** Redireciona para a tela de cadastro de paciente (acesso público)

**Credenciais de Teste:**
| Campo | Valor |
|-------|-------|
| CPF   | `00000000000` |
| Senha | `admin123` |

> **Nota:** Se o CPF estiver cadastrado em mais de um tipo de usuário (ex: Médico e Gerente), o sistema exibirá um seletor para escolher qual perfil deseja usar.

---

### 3.2. Seletor de Papéis

Quando um CPF está cadastrado em múltiplos perfis, o sistema exibe esta tela:

**Opções disponíveis:**
- **Gerente:** Acesso ao painel administrativo, cadastros e relatórios
- **Médico:** Acesso às telas e rotinas de atendimento médico
- **Paciente:** Acesso ao painel do paciente e suas informações

---

### 3.3. Dashboard (Gerente)

O dashboard do gerente exibe um resumo das atividades da clínica:

**Indicadores:**
- Total de Médicos
- Total de Pacientes
- Consultas Pendentes
- Consultas Concluídas

**Navegação:** Menu lateral com acesso a todas as funcionalidades.

---

### 3.4. Gerenciamento de Médicos

**Funcionalidades:**
- **Listar:** Visualiza todos os médicos cadastrados
- **Cadastrar:** Adiciona um novo médico (+ Novo Médico)
- **Editar:** Altera os dados de um médico existente (incluindo senha)
- **Excluir:** Remove um médico do sistema

**Campos do médico:**
- Nome completo
- CPF
- Endereço (Rua, Número, Bairro, Cidade, Estado)
- CRM / Código do Conselho
- Valor da consulta

---

### 3.5. Gerenciamento de Pacientes

**Funcionalidades:**
- **Listar:** Visualiza todos os pacientes cadastrados
- **Cadastrar:** Adiciona um novo paciente (+ Novo Paciente)
- **Editar:** Altera os dados de um paciente existente (incluindo senha)
- **Excluir:** Remove um paciente do sistema
- **Ver Prontuários:** Visualiza os prontuários do paciente

**Campos do paciente:**
- Nome completo
- CPF
- Endereço (Rua, Número, Bairro, Cidade, Estado)

---

### 3.6. Gerenciamento de Gerentes

**Funcionalidades:**
- **Listar:** Visualiza todos os gerentes cadastrados
- **Cadastrar:** Adiciona um novo gerente (+ Novo Gerente)
- **Editar:** Altera os dados de um gerente existente (incluindo senha)
- **Excluir:** Remove um gerente do sistema

**Campos do gerente:**
- Nome completo
- CPF
- Endereço (Rua, Número, Bairro, Cidade, Estado)

---

### 3.7. Gerenciamento de Consultas

**Funcionalidades:**
- **Listar:** Visualiza todas as consultas agendadas
- **Agendar:** Cria uma nova consulta
- **Editar:** Altera os dados de uma consulta
- **Cancelar:** Cancela uma consulta
- **Concluir:** Marca uma consulta como concluída

**Status das Consultas:**
| Status | Descrição |
|--------|-----------|
| `SCHEDULED` | Agendada (padrão) |
| `COMPLETED` | Concluída |
| `CANCELED` | Cancelada |

---

### 3.8. Tela de Cadastro de Consulta

**Campos:**
- **Paciente:** Selecione o paciente (com busca em tempo real)
- **Médico:** Selecione o médico (com busca em tempo real)
- **Data:** Selecione a data da consulta
- **Hora:** Selecione o horário da consulta
- **Status:** Status da consulta (padrão: `SCHEDULED`)

---

### 3.9. Tela do Médico

O médico tem acesso a funcionalidades específicas para atendimento:

**Menu:**
- **Meus Pacientes:** Lista os pacientes vinculados ao médico
- **Minhas Consultas:** Lista todas as consultas do médico
- **Cadastrar Prontuário:** Registra um prontuário para um paciente
- **Gerar Relatórios:** Gera relatórios de atendimento

#### 3.9.1. Minhas Consultas

Lista todas as consultas do médico logado, exibindo:
- Data/Hora
- Paciente
- Status
- Ações (Ver detalhes, Concluir, Cancelar)

#### 3.9.2. Cadastrar Prontuário

**Campos:**
- **Paciente selecionado:** (selecionado pela lista lateral)
- **Data da consulta:** Data da consulta
- **Observações Médicas:** Descrição detalhada da consulta

**Funcionalidade de busca:**
- Busca em tempo real por nome ou CPF
- Clique no paciente para selecioná-lo

> 💡 **Dica:** Seja detalhado nas observações. Inclua sintomas relatados, exames físicos realizados, diagnóstico, prescrições e orientações ao paciente.

---

### 3.10. Tela do Paciente

O paciente tem acesso a funcionalidades específicas:

**Menu:**
- **Dashboard:** Visão geral do paciente
- **Meus Prontuários:** Visualiza seus prontuários
- **Minhas Consultas:** Visualiza suas consultas
- **Editar Dados:** Altera seus dados pessoais
- **Agendar Consulta:** Solicita uma nova consulta

#### 3.10.1. Agendar Consulta

**Campos:**
- **Médico:** Selecione o médico (com busca em tempo real)
- **Data:** Selecione a data da consulta
- **Hora:** Selecione o horário da consulta
- **Observações:** Descreva o motivo da consulta

**Funcionalidade:**
- Lista lateral de médicos disponíveis
- Busca em tempo real por nome ou CRM
- Clique no médico para selecioná-lo

> 💡 **Dica:** Escolha uma data com pelo menos 24 horas de antecedência. Você pode cancelar a consulta até 2 horas antes do horário agendado.

---

## 4. Funcionalidades Gerais

### 4.1. Busca

A tela de busca permite pesquisar por:
- Médicos (por nome, CPF ou CRM)
- Pacientes (por nome ou CPF)

**Funcionalidades:**
- Busca com filtro por tipo
- Visualização de detalhes
- Seleção de resultados

---

### 4.2. Relatórios

Gera relatórios de consultas por médico em um período específico.

**Informações do relatório:**
- Total de consultas
- Consultas agendadas
- Consultas concluídas
- Consultas canceladas

---

### 4.3. Perfil do Usuário

Permite ao usuário visualizar e editar seus próprios dados.

---

## 5. Tutorial de Uso

### 5.1. Como Agendar uma Consulta como Gerente

1. Faça login como gerente (CPF: `00000000000`, Senha: `admin123`)
2. No menu lateral, clique em **"Consultas"**
3. Clique em **"Agendar Consulta"**
4. Selecione o **Paciente** (digite para buscar)
5. Selecione o **Médico** (digite para buscar)
6. Escolha a **Data** e **Hora**
7. Selecione o **Status** (padrão: `SCHEDULED`)
8. Clique em **"Agendar Consulta"**

---

### 5.2. Como Cadastrar um Prontuário como Médico

1. Faça login como médico (CPF: `09749723473`, Senha: `123`)
2. No menu lateral, clique em **"Cadastrar Prontuário"**
3. Na lista lateral, busque o paciente por nome ou CPF
4. Clique no paciente para selecioná-lo
5. Selecione a **Data** da consulta
6. Digite as **Observações Médicas**
7. Clique em **"Salvar Prontuário"**

---

### 5.3. Como Agendar uma Consulta como Paciente

1. Faça login como paciente (CPF: `09749723473`, Senha: `123`)
2. No menu lateral, clique em **"Minhas Consultas"**
3. Clique em **"Agendar Consulta"**
4. Na lista lateral, busque o médico por nome ou CRM
5. Clique no médico para selecioná-lo
6. Escolha a **Data** e **Hora**
7. Digite as **Observações** (motivo da consulta)
8. Clique em **"Agendar Consulta"**

---

### 5.4. Como Cadastrar um Novo Paciente

1. Faça login como gerente
2. No menu lateral, clique em **"Pacientes"**
3. Clique em **"+ Novo Paciente"**
4. Preencha todos os campos obrigatórios (`*`)
5. Clique em **"Cadastrar Paciente"**

---

## 6. Estrutura de Dados

### 6.1. Tabelas do Banco de Dados

| Tabela | Descrição |
|--------|-----------|
| `addresses` | Endereços dos usuários |
| `manager` | Gerentes do sistema |
| `doctor` | Médicos da clínica |
| `patient` | Pacientes da clínica |
| `consultation` | Consultas agendadas |
| `medical_records` | Prontuários médicos |

---

### 6.2. Relacionamentos

```
addresses (1) ─── (N) manager
addresses (1) ─── (N) doctor
addresses (1) ─── (N) patient

patient (1) ─── (N) consultation
doctor  (1) ─── (N) consultation

patient (1) ─── (N) medical_records
doctor  (1) ─── (N) medical_records
```

---

### 6.3. Status das Consultas

| Status | Descrição |
|--------|-----------|
| `SCHEDULED` | Consulta agendada |
| `COMPLETED` | Consulta concluída |
| `CANCELED` | Consulta cancelada |

---

## 7. Segurança

### 7.1. Autenticação

O sistema utiliza autenticação por CPF e senha:
- As senhas são armazenadas com hash **SHA-256**
- A senha padrão do administrador é `admin123`

### 7.2. Perfis de Acesso

| Perfil | Acesso |
|--------|--------|
| **Gerente** | Acesso total ao sistema |
| **Médico** | Acesso a pacientes, consultas e prontuários |
| **Paciente** | Acesso apenas aos seus próprios dados |

---

## 8. Telas de Cadastro

### 8.1. Cadastro de Paciente (Acesso Público)

**Acesso:** Tela de Login → "Cadastrar novo paciente"

**Campos obrigatórios (`*`):**
- Nome completo
- CPF
- Senha (mínimo 6 caracteres)
- Confirmar senha
- Rua, Número, Bairro, Cidade, Estado

**Fluxo:**
1. Preencha todos os campos
2. Clique em "Cadastrar"
3. Faça login com as credenciais criadas

---

### 8.2. Cadastro de Médico (Gerente)

**Acesso:** Tela de Médicos → "+ Novo Médico"

**Campos obrigatórios (`*`):**
- Nome completo
- CPF
- CRM / Código do Conselho
- Valor da consulta
- Endereço completo

---

### 8.3. Cadastro de Gerente (Gerente)

**Acesso:** Tela de Gerentes → "+ Novo Gerente"

**Campos obrigatórios (`*`):**
- Nome completo
- CPF
- Endereço completo

---

## 9. Atalhos do Teclado

| Ação | Atalho |
|------|--------|
| Entrar (Login) | `Enter` |
| Salvar | `Ctrl + S` |
| Cancelar | `Esc` |

---

## 10. Perguntas Frequentes (FAQ)

### 10.1. Por que o paciente não aparece no seletor de papéis?

Verifique se:
- O CPF do paciente está cadastrado na tabela `patient`
- A senha do paciente é a mesma que você está usando no login
- O paciente tem um endereço válido associado

Para corrigir a senha do paciente:

```sql
UPDATE patient 
SET password = '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9' 
WHERE cpf = '09749723473';
```

---

### 10.2. Não consigo fazer login, o que fazer?

1. Verifique se o CPF está correto (apenas números)
2. Verifique se a senha está correta
3. Verifique se o banco de dados está rodando
4. Tente as credenciais padrão: CPF `00000000000`, Senha `admin123`

---

### 10.3. Como resetar a senha de um usuário?

1. Faça login como gerente
2. Acesse a tela do tipo de usuário (Médicos, Pacientes ou Gerentes)
3. Clique em "Editar" do usuário desejado
4. Preencha o campo **"Nova senha (opcional)"** com a nova senha
5. Confirme a senha
6. Clique em **"Salvar alterações"**

---

### 10.4. O que fazer se o sistema estiver lento?

1. Verifique a conexão com o banco de dados
2. Feche outros programas
3. Reinicie a aplicação
4. Verifique se há muitas consultas na tabela

---

*Hospital Manager — Sistema de Gestão Hospitalar | UFERSA*
