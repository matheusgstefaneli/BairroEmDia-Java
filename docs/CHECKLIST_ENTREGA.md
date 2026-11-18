# Checklist de entrega — Bairro em Dia (GS 2026.2)

> Prazo: confira no Portal do Aluno. **Commits depois do prazo podem zerar a parte técnica.**

## 1. Banco Oracle (você)
- [ ] Criar a conexão no Oracle SQL Developer com os dados da FIAP.
- [ ] Executar `sql/01_create_tables.sql` (e, se quiser, `sql/02_seed.sql`).
- [ ] Conferir: `SELECT * FROM TB_MORADOR;` roda sem erro.

## 2. Rodar localmente (você)
- [ ] Abrir a pasta no IntelliJ (JDK 21), esperar o Maven baixar as dependências.
- [ ] Definir `DB_URL`, `DB_USER`, `DB_PASSWORD` na configuração de execução.
- [ ] Rodar `BairroEmDiaApplication` e abrir http://localhost:8080/
- [ ] Testar os 4 verbos (GET, POST, PUT, DELETE) nos 3 recursos (pasta `http/`).
- [ ] Rodar `mvn test` e **salvar a saída** (ex.: `mvn test > docs/evidencias.txt`).

## 3. GitHub (você)
- [ ] Criar repositório no GitHub (sem README automático).
- [ ] `git init`, `git add .`, `git commit`, `git remote add origin ...`, `git push -u origin main`
- [ ] Conferir no site que NÃO há senha/URL de banco no código.

## 4. Render (você)
- [ ] New > Web Service > repositório > Runtime **Docker** > plano Free.
- [ ] Variáveis: `DB_URL`, `DB_USER`, `DB_PASSWORD`.
- [ ] Health Check Path: `/`
- [ ] Testar na URL pública: `/`, `/categorias`, `/moradores`, POST e DELETE.
- [ ] Se `/moradores` der 500: ver *Logs* (provável bloqueio de rede/credenciais do Oracle).

## 5. Documentação e entrega
- [ ] Gerar o PDF final com os links reais e as evidências:
      `GITHUB_URL=... RENDER_URL=... python3 docs/gerar_pdf.py` (ou peça para regenerar)
- [ ] Conferir: capa (nome, RM), índice, diagramas, protótipos, endpoints, instruções.
- [ ] ZIP final: PDF + projeto Java completo + links (GitHub e deploy).
- [ ] Enviar pelo Portal do Aluno antes do prazo.
