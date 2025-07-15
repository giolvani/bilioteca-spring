# Biblioteca Spring

Um sistema robusto e modular de gerenciamento de biblioteca desenvolvido com Spring Boot.

## Funcionalidades
- Gerencie livros, usuários e empréstimos
- Endpoints RESTful
- Arquitetura modular e extensível
- Fácil integração com bancos de dados

## Requisitos
- Java 17 ou superior
- Maven 3.6+

## Instalação e Configuração
1. **Clone o repositório:**
   ```bash
   git clone https://github.com/seudominio/biblioteca_spring.git
   cd biblioteca_spring
   ```
2. **Compile o projeto:**
   ```bash
   ./mvnw clean install
   ```
3. **Execute a aplicação:**
   ```bash
   ./mvnw spring-boot:run
   ```

## Uso
- A API estará disponível em `http://localhost:8080` por padrão.
- Utilize ferramentas como Postman ou curl para interagir com os endpoints.
- Exemplos de endpoints:
  - `GET /books` - Lista todos os livros
  - `POST /books` - Adiciona um novo livro
  - `GET /users` - Lista todos os usuários

## Estrutura do Projeto
```
├── src/main/java
│   └── ... (código-fonte Java)
├── src/main/resources
│   └── application.properties
├── pom.xml
└── README.md
```

## Informações Acadêmicas
Este projeto foi desenvolvido como trabalho acadêmico para a disciplina de Web III da IFPR.

## Contato
Para questões relacionadas a este trabalho: [seu.email@exemplo.com](mailto:seu.email@exemplo.com)