<p align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java">
  <img src="https://img.shields.io/badge/JDK-21+-informational?style=for-the-badge" alt="JDK 21+">
  <img src="https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Boot">
<img src="https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL">
  <img src="https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white" alt="Docker">
</p>
<h1 align="center">📖 Gerenciador de Bibliotecas 📚</h1>

## ℹ️ Sobre o Projeto

Este projeto consiste em uma API REST desenvolvida com **Spring Boot** para o gerenciamento de bibliotecas. O sistema administra desde o cadastro de usuários e livros até as lógicas de empréstimo de livros.

> **🛡️ Aviso sobre privacidade:** Este projeto foi desenvolvido para fins educacionais. Os dados utilizados (como CPFs, e-mails e senhas) são fictícios, não representando informações de pessoas reais.

⚠️ O sistema ainda está em fase de desenvolvimento.

## 🔨 Roadmap de Desenvolvimento

### Geral
- [x] Configuração inicial do projeto (Spring initializr, dependências)
- [ ] Finalizar Entidade User
- [ ] Finalizar Entidade Book
- [ ] Finalizar Entidade Loan
- [ ] Documentação com Swagger

### 🧑 Entidade User (Usuário)
- [x] Criação da Entidade
- [x] CRUD completo (Criar, Consultar, Atualizar e Excluir)
- [x] Adicionar Autenticação com Spring Security
- [x] Adicionar Criptografia de senhas
- [ ] Testes unitários

### 📚 Entidade Book (Livro)
- [ ] Criação da Entidade
- [ ] CRUD completo (Criar, Consultar, Atualizar e Excluir)
- [ ] Testes unitários

### 🤝 Entidade Loan (Empréstimo)
- [ ] Criação da Entidade e os seus relacionamentos
- [ ] Fluxo de Empréstimos (Registrar, Consultar, Renovar, Finalizar)
- [ ] Testes unitários


### 🗃️ Arquitetura do banco de dados
```mermaid
erDiagram
    USER ||--o{ LOAN : makes
    BOOK ||--o{ LOAN : is_in
    
USER {
bigint id PK
date birth_date
varchar cpf UK
datetime created_at
varchar email UK
varchar full_name
varchar password
varchar role
datetime updated_at
}
```

(Esta seção será atualizada conforme o desenvolvimento da API)

### 📂 Estrutura do Projeto
```
.
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── librarymanager/
│   │   │       ├── config/                             # Classes de configurações 
│   │   │       ├── controller/                         # Endpoints da API
│   │   │       ├── domain/                             # Entidades JPA e Enumerações
│   │   │       ├── dto/                                # Objetos de transferência de dados
│   │   │       ├── exception/                          # Exceções e GlobalHandlerException
│   │   │       ├── mapper/                             # Mappers
│   │   │       ├── repository/                         # Comunicação com o banco de dados
│   │   │       ├── security/                           # Configurações e camadas de segurança da API
│   │   │       ├── service/                            # Regras de negócio do sistema
│   │   │       └── LibraryManagerApiApplication.java   # Inicialização da Aplicação
│   │   └── resources/                                  # Perfis de ambiente e chaves de segurança RSA
│   └── test                                            # Testes unitários
├── compose.yaml                                        # Organização dos containers
├── Dockerfile                                          # Criação da imagem da API
├── pom.xml                                             # Dependências do projeto
├── .dockerignore                                       # Exclusão de arquivos desnecessários na imagem Docker 
├── .envTemplate                                        # Template das variáveis de ambiente
├── .gitignore 
└── README.md
```

(Esta seção será atualizada conforme o desenvolvimento da API)

### 🛠️ Tecnologias e ferramentas

**Linguagem:** Java 21

**Framework:** Spring Boot 3

**Persistência:** Spring Data JPA / Hibernate

**Banco de dados:** MySQL 8

**Infraestrutura:** Docker & Docker Compose

**Padrão de Camadas:** Arquitetura em camadas (Controller, Service, Repository e Entity)

## 🚀 Executando a Aplicação

![WIP](https://img.shields.io/badge/status-em%20constru%C3%A7%C3%A3o-lightgrey?style=flat-square)

(Esta seção será atualizada com o passo a passo para utilizar a API)
