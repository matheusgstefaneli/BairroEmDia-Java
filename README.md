# Bairro em Dia — CitizenTech

API RESTful para registro, acompanhamento e priorização de problemas urbanos (buracos, postes sem luz, calçadas danificadas, lixo acumulado etc.).

**Global Solution 2026.2 — FIAP | Domain Driven Design (Java)**
Professor: Gilberto Alexandre das Neves · Turma 2ESPI
Aluno: **Matheus Gomes Stefaneli** · RM **562277** · Desenvolvimento individual

- Repositório GitHub: `<PREENCHER: link do repositório>`
- Deploy (Render): `<PREENCHER: link do serviço>`

## Arquitetura

```
Controller  ->  BO  ->  DAO  ->  Oracle FIAP
(HTTP/JSON)   (regras)  (persistência)
```

| Pacote | Responsabilidade |
|---|---|
| `model` | Entidades JPA (`Morador`, `Categoria`, `Ocorrencia`) e enum `StatusOcorrencia` |
| `dao` | Classes DAO. Cada uma encapsula um `JpaRepository` (pacote `dao.repository`); a BO nunca usa o repository diretamente |
| `bo` | Regras de negócio |
| `controller` | Endpoints REST (sem regra de negócio) |
| `dto` | Objetos de entrada/saída com Bean Validation |
| `exception` | Exceções personalizadas + `@RestControllerAdvice` |
| `config` | CORS |

## Regras de negócio principais
- Ocorrência nasce com status `ABERTA` e data de criação definida pelo sistema.
- Fluxo de status: `ABERTA -> EM_ANALISE -> EM_ANDAMENTO -> RESOLVIDA`; `CANCELADA` a partir de qualquer estado não final.
- Ocorrência `RESOLVIDA` ou `CANCELADA` não pode mais ser alterada (409).
- Prioridade de 1 a 5.
- Ocorrência `EM_ANDAMENTO` não pode ser excluída (409).
- Morador/categoria com ocorrências vinculadas não podem ser excluídos (409); e-mail de morador e nome de categoria são únicos (409).

## Endpoints

| Método | Rota | Descrição | Sucesso |
|---|---|---|---|
| GET | `/` | Status da API | 200 |
| GET | `/moradores` | Lista moradores | 200 |
| GET | `/moradores/{id}` | Busca por id | 200 |
| POST | `/moradores` | Cria morador | 201 |
| PUT | `/moradores/{id}` | Atualiza morador | 200 |
| DELETE | `/moradores/{id}` | Exclui morador | 204 |
| GET | `/categorias` | Lista categorias | 200 |
| GET | `/categorias/{id}` | Busca por id | 200 |
| POST | `/categorias` | Cria categoria | 201 |
| PUT | `/categorias/{id}` | Atualiza categoria | 200 |
| DELETE | `/categorias/{id}` | Exclui categoria | 204 |
| GET | `/ocorrencias` | Lista ocorrências | 200 |
| GET | `/ocorrencias/{id}` | Busca por id | 200 |
| POST | `/ocorrencias` | Cria ocorrência | 201 |
| PUT | `/ocorrencias/{id}` | Atualiza ocorrência | 200 |
| PATCH | `/ocorrencias/{id}/status` | Altera só o status | 200 |
| PATCH | `/ocorrencias/{id}/prioridade` | Altera só a prioridade | 200 |
| DELETE | `/ocorrencias/{id}` | Exclui ocorrência | 204 |

Erros: 400 (dados inválidos), 404 (não encontrado), 405, 409 (conflito/regra de negócio), 500 (erro interno, sem detalhes internos). Formato:

```json
{ "timestamp": "...", "status": 404, "erro": "Not Found", "mensagem": "Morador com id 9 não encontrado(a).", "caminho": "/moradores/9", "detalhes": [] }
```

Exemplos de requisições: pasta `http/` (arquivo `.http` do IntelliJ e coleção Postman/Insomnia).

## Requisitos
- JDK 21
- Maven 3.9+ (o IntelliJ já traz um Maven embutido) — ou Maven Wrapper (veja abaixo)
- Spring Boot 4.1.2 (parent do `pom.xml`; o material da aula usa 4.1.0 — mesma linha 4.1.x)
- Oracle FIAP (acesso via Oracle SQL Developer) e driver `ojdbc11` (já no `pom.xml`)

## Como executar localmente
1. No Oracle SQL Developer, conectado ao banco da FIAP, execute `sql/01_create_tables.sql` (e, se quiser dados de exemplo, `sql/02_seed.sql`).
2. Defina as variáveis de ambiente (use os dados da **sua** conexão do SQL Developer):
   - `DB_URL` = `jdbc:oracle:thin:@HOST:PORTA/SERVICE_NAME` (ou `@HOST:PORTA:SID`, conforme sua conexão)
   - `DB_USER` e `DB_PASSWORD`
   No IntelliJ: *Run > Edit Configurations > Environment variables*.
3. Execute a classe `BairroEmDiaApplication` ou:
   ```
   mvn clean -DskipTests package
   java -jar target/bairro-em-dia.jar
   ```
4. Acesse `http://localhost:8080/`.

### Maven Wrapper
Este ZIP não traz os scripts `mvnw`. Para gerar (opcional), dentro da pasta do projeto: `mvn wrapper:wrapper`. Depois: `./mvnw clean -DskipTests package` (Linux/Mac) ou `mvnw.cmd clean -DskipTests package` (Windows).

## Testes
```
mvn test
```
Os testes de API usam H2 em memória (perfil `test`) — **somente nos testes**. `OracleConexaoTest` só roda se `DB_URL` estiver definida e valida a conexão real com o Oracle.

## Deploy no Render
O Render não possui runtime Java nativo, por isso o projeto usa `Dockerfile`.
1. Suba o projeto no GitHub (ver abaixo).
2. No Render: **New > Web Service** > conecte o repositório > *Runtime/Language*: **Docker** > plano *Free*.
3. Em *Environment*, crie `DB_URL`, `DB_USER`, `DB_PASSWORD` (mesmos valores do passo local).
4. (Opcional) *Health Check Path*: `/`.
5. Aguarde o deploy e teste: `https://SEU-SERVICO.onrender.com/` e `/ocorrencias`.

**Atenção:** o Render precisa conseguir alcançar o Oracle da FIAP pela internet. Se `/` responde mas `/moradores` retorna 500, veja os *Logs* do Render: normalmente é a conexão com o banco (host/porta/serviço errados, firewall ou credenciais). O plano free "dorme" após inatividade; a primeira requisição pode levar cerca de 1 minuto.

## GitHub
```
git init
git add .
git commit -m "Bairro em Dia - Global Solution 2026.2"
git branch -M main
git remote add origin <URL-DO-SEU-REPOSITORIO>
git push -u origin main
```
Nunca faça commit de credenciais (o `.gitignore` já ignora `.env`). **Commits após o prazo podem zerar a parte técnica.**
