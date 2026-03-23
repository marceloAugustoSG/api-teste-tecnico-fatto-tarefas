# API Lista de Tarefas

API REST em **Java 21** e **Spring Boot 4** para cadastro e gestão de tarefas, com persistência em **PostgreSQL**, migrações com **Liquibase**, documentação de contrato em **OpenAPI** e suporte a **execução em Docker** (desenvolvimento local e deploy em nuvem).

---

## O que foi implementado

- **CRUD de tarefas** com regras de negócio: nome único, custo ≥ 0, data limite obrigatória, ordem de apresentação única e reordenação (subir/descer).
- **Respostas no padrão brasileiro** para valores monetários e datas na camada de API.
- **Destaque de alto custo** (`altoCusto`) quando o valor é ≥ R$ 1.000,00 (para o front aplicar estilo, ex.: fundo amarelo).
- **Tratamento de erros** com **RFC 7807** (`ProblemDetail`): 400 (validação/regra), 404, 409 (duplicidade/conflito).
- **CORS** configurável (desenvolvimento local e produção via variáveis de ambiente).
- **Migrações de banco** com Liquibase (XML em `src/main/resources/liquibase/`).
- **Contrato OpenAPI** versionado em `openapi.yaml` na raiz do repositório.
- **Testes automatizados** (unitários, teste de fatia do MVC, JPA e integração HTTP).
- **Docker**: `docker-compose` para PostgreSQL local; `Dockerfile` multi-stage para build e execução da aplicação; blueprint de exemplo para **Render** (`render.yaml`).
- **Deploy**: perfil `prod` com variáveis para banco gerenciado (ex.: **Neon**) e **Render**.

---

## Estrutura do projeto e responsabilidades


| Local                        | Responsabilidade                                                                                              |
| ---------------------------- | ------------------------------------------------------------------------------------------------------------- |
| `Application.java`           | Ponto de entrada Spring Boot (`@SpringBootApplication`).                                                      |
| `controller/`                | Camada HTTP: mapeamento de rotas, status codes, delegação ao serviço; validação de entrada (`@Valid`).        |
| `service/`                   | Regras de negócio, transações (`@Transactional`), orquestração do repositório e montagem de DTOs de resposta. |
| `repository/`                | Acesso a dados (Spring Data JPA).                                                                             |
| `entity/`                    | Mapeamento JPA da tabela `tarefas`.                                                                           |
| `dto/`                       | Contratos de entrada/saída da API (records).                                                                  |
| `exception/`                 | Exceções de domínio e `GlobalExceptionHandler` (mapeamento para `ProblemDetail`).                             |
| `config/`                    | Configurações transversais (ex.: CORS).                                                                       |
| `util/`                      | Utilitários compartilhados (ex.: formatação pt-BR).                                                           |
| `src/main/resources/`        | `application.properties`, perfil `application-prod.properties`, changelogs Liquibase.                         |
| `src/test/java/.../support/` | Factories de teste (entidades, DTOs, JSON) para reutilização nos testes.                                      |


---

## Endpoints da API

Base path: `**/api/tarefas`** (ex.: `http://localhost:8080`).


| Método   | Caminho                    | Descrição                                                                         |
| -------- | -------------------------- | --------------------------------------------------------------------------------- |
| `GET`    | `/api/tarefas`             | Lista todas as tarefas (ordenadas por apresentação) e o somatório dos custos.     |
| `POST`   | `/api/tarefas`             | Inclui tarefa (corpo: `nome`, `custo`, `dataLimite` em ISO `yyyy-MM-dd`). **201** |
| `PUT`    | `/api/tarefas/{id}`        | Atualiza nome, custo e data limite. **200**                                       |
| `DELETE` | `/api/tarefas/{id}`        | Exclui a tarefa. **204**                                                          |
| `POST`   | `/api/tarefas/{id}/subir`  | Sobe uma posição na ordem. **204**                                                |
| `POST`   | `/api/tarefas/{id}/descer` | Desce uma posição na ordem. **204**                                               |


**Swagger / OpenAPI em runtime:** podem estar desativados no perfil `prod` (`springdoc.*.enabled=false`). O contrato estático permanece em `**openapi.yaml`**.

---

## Testes

A suíte cobre:

- **Serviço** (`TarefaServiceTest`): regras com **Mockito** (duplicidade, ordem, exclusão, etc.).
- **Controller** (`TarefaControllerTest`): `**@WebMvcTest`** com serviço mockado (HTTP e validação).
- **Repositório** (`TarefaRepositoryTest`): `**@DataJpaTest`** com H2 (perfil `test`).
- **Integração** (`TarefaApiIT`): `**@SpringBootTest`** + **MockMvc**, fluxo completo contra a API com banco H2 em memória.

Factories em `src/test/java/.../support/` centralizam criação de dados e JSON nos testes.

Executar todos os testes:

```bash
./mvnw test
```

(Windows: `mvnw.cmd test`.)

---

## Docker

### Banco PostgreSQL (desenvolvimento local)

Sobe apenas o PostgreSQL (dados persistidos em volume):

```bash
docker compose up -d
```

Credenciais padrão alinhadas ao `application.properties`: usuário `postgres`, senha `postgres`, banco `lista_tarefas`, porta **5432** em `127.0.0.1`.

### Imagem da aplicação (build e execução)

O `**Dockerfile`** na raiz:

- Compila o projeto com **Maven** (imagem `maven:3.9.9-eclipse-temurin-21-alpine`).
- Gera imagem final com **JRE 21**, usuário não-root e `SPRING_PROFILES_ACTIVE=prod`.

Útil para deploy em plataformas que constroem a partir do Dockerfile (ex.: **Render**).

### Deploy (Render + Neon)

- Configure `**DATABASE_URL`** (JDBC do Neon, com `sslmode=require`; usuário e senha podem ir na URL ou em `DATABASE_USER` / `DATABASE_PASSWORD`, conforme `application-prod.properties`).
- `**CORS_ALLOWED_ORIGIN_PATTERNS**`: origens do front em produção (separadas por vírgula).
- O Render injeta `**PORT**`; a aplicação já usa `server.port=${PORT}` no perfil `prod`.

O arquivo `**render.yaml**` é um blueprint de exemplo; ajuste nomes e secrets no painel do Render.  
  
O Deploy foi feito com Render para hospedar o web-service que é a API, e o banco de dados foi hospedado no Neon

---

## Como rodar localmente (sem Docker só para o banco)

1. Java **21** e **Maven** (ou use `./mvnw`).
2. Suba o Postgres com `docker compose up -d` **ou** aponte `DATABASE_URL` / usuário / senha no `application.properties`.
3. Execute:

```bash
./mvnw spring-boot:run
```

A API sobe em `**http://localhost:8080**` (porta padrão, se não houver variável `PORT`).

---

## Requisitos

- **Java 21**
- **Maven 3.9+** (ou Maven Wrapper incluído no repositório)
- **PostgreSQL** (local via Docker Compose ou serviço gerenciado)

---

