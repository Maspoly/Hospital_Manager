# 🏥 Sistema de Gerenciamento de Consultório Médico

## 📌 Descrição
Este projeto consiste em um sistema de gerenciamento de consultório médico desenvolvido para auxiliar no controle de pacientes, médicos, consultas e prontuários.  
O sistema foi pensado para atender as necessidades do Dr. Luiz, permitindo organização, agendamento e acompanhamento de atendimentos.

---

## 🚀 Funcionalidades

### ✅ CRUD Completo
- **Médicos**
  - Nome, CPF, endereço
  - Valor da consulta
  - Código do conselho
  - ⚠️ Apenas gerentes podem cadastrar

- **Pacientes**
  - Nome, CPF, endereço
  - Prontuário vinculado

- **Consultas**
  - Paciente
  - Médico
  - Data e hora

- **Prontuários**
  - Data
  - Observações médicas

---

### 🔍 Sistema de Busca
- Buscar paciente por:
  - Nome ou CPF
- Buscar médico por:
  - Nome ou código do conselho
- Buscar consultas por:
  - Médico
  - Paciente
  - Data/horário

---

### 📅 Gestão de Consultas
- Marcar consultas
- Cancelar consultas
- Acompanhar status:
  - Agendada
  - Concluída
  - Cancelada

---

### 🧾 Prontuários
- Médico pode:
  - Criar prontuário durante consulta
  - Editar observações
  - Excluir prontuários
- Histórico de atendimentos armazenado

---

### 📊 Relatórios
- Consultas por médico em determinado período
- Estatísticas:
  - Total de consultas
  - Agendadas
  - Concluídas
  - Canceladas
- Exportação (CSV)

---

### 🔐 Controle de Acesso

#### 👨‍💼 Gerente
- Cadastrar médicos
- Gerenciar sistema completo

#### 👨‍⚕️ Médico
- Visualizar pacientes
- Gerenciar prontuários
- Editar dados dos pacientes
- Gerar relatórios
- Acessar apenas suas consultas

---

## 💻 Tecnologias Utilizadas
- JAVA (Até o momento)

