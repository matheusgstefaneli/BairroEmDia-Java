# Gera o PDF único de documentação. Uso:
#   GITHUB_URL=... RENDER_URL=... python3 gerar_pdf.py
# Se existir docs/evidencias.txt, o conteúdo é incluído literalmente na seção "Evidências".
import os, sys
from reportlab.lib.pagesizes import A4, landscape
from reportlab.lib.units import cm
from reportlab.lib import colors
from reportlab.lib.styles import ParagraphStyle
from reportlab.lib.enums import TA_CENTER, TA_JUSTIFY
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import (BaseDocTemplate, PageTemplate, Frame, Paragraph, Spacer, PageBreak, Image,
                                Table, TableStyle, Preformatted, NextPageTemplate, KeepTogether)
from reportlab.platypus.tableofcontents import TableOfContents
from PIL import Image as PILImage

HERE = os.path.dirname(os.path.abspath(__file__))
FD = "/usr/share/fonts/truetype/dejavu/"
pdfmetrics.registerFont(TTFont("DV", FD + "DejaVuSans.ttf"))
pdfmetrics.registerFont(TTFont("DV-B", FD + "DejaVuSans-Bold.ttf"))
pdfmetrics.registerFont(TTFont("DV-I", FD + "DejaVuSans-Oblique.ttf"))
pdfmetrics.registerFont(TTFont("DV-BI", FD + "DejaVuSans-BoldOblique.ttf"))
pdfmetrics.registerFont(TTFont("DVM", FD + "DejaVuSansMono.ttf"))
pdfmetrics.registerFontFamily("DV", normal="DV", bold="DV-B", italic="DV-I", boldItalic="DV-BI")

GITHUB = os.environ.get("GITHUB_URL", "").strip()
RENDER = os.environ.get("RENDER_URL", "").strip()
AZ = colors.HexColor("#1F4E79")

ALUNO = "Matheus Gomes Stefaneli"; RM = "562277"; TURMA = "2ESPI"
PROF = "Gilberto Alexandre das Neves"

st = {
 "body": ParagraphStyle("body", fontName="DV", fontSize=9.5, leading=14, alignment=TA_JUSTIFY, spaceAfter=6),
 "h1": ParagraphStyle("h1", fontName="DV-B", fontSize=16, leading=20, textColor=AZ, spaceBefore=4, spaceAfter=10),
 "h2": ParagraphStyle("h2", fontName="DV-B", fontSize=11.5, leading=15, textColor=AZ, spaceBefore=8, spaceAfter=5),
 "cap": ParagraphStyle("cap", fontName="DV-I", fontSize=8, leading=11, alignment=TA_CENTER, textColor=colors.HexColor("#555555"), spaceAfter=8),
 "cell": ParagraphStyle("cell", fontName="DV", fontSize=7.8, leading=10),
 "cellb": ParagraphStyle("cellb", fontName="DV-B", fontSize=7.8, leading=10, textColor=colors.white),
 "bul": ParagraphStyle("bul", fontName="DV", fontSize=9.5, leading=13.5, leftIndent=14, bulletIndent=3, spaceAfter=2),
 "code": ParagraphStyle("code", fontName="DVM", fontSize=7.3, leading=9.2, backColor=colors.HexColor("#F4F4F4"),
                        borderColor=colors.HexColor("#CCCCCC"), borderWidth=0.5, borderPadding=5, spaceAfter=10, spaceBefore=2),
 "warn": ParagraphStyle("warn", fontName="DV", fontSize=9, leading=13, backColor=colors.HexColor("#FFF4E5"),
                        borderColor=colors.HexColor("#E0A030"), borderWidth=0.8, borderPadding=6, spaceAfter=10, spaceBefore=4),
}
def P(t, s="body"): return Paragraph(t, st[s])
def H1(t): return Paragraph(t, st["h1"])
def H2(t): return Paragraph(t, st["h2"])
def B(items): return [Paragraph(i, st["bul"], bulletText="•") for i in items]
def code(t): return Preformatted(t.strip("\n"), st["code"])
def link_txt(v, rotulo):
    if v: return f'<a href="{v}" color="#1F4E79">{v}</a>'
    return f'<font color="#B00020"><b>[PREENCHER: {rotulo} — informe antes de entregar]</b></font>'

def tabela(dados, larguras, cab=True):
    linhas = []
    for i, l in enumerate(dados):
        linhas.append([Paragraph(str(c), st["cellb"] if (cab and i == 0) else st["cell"]) for c in l])
    t = Table(linhas, colWidths=[w * cm for w in larguras], repeatRows=1 if cab else 0)
    est = [("GRID", (0, 0), (-1, -1), 0.4, colors.HexColor("#AAAAAA")), ("VALIGN", (0, 0), (-1, -1), "TOP"),
           ("TOPPADDING", (0, 0), (-1, -1), 3), ("BOTTOMPADDING", (0, 0), (-1, -1), 3)]
    if cab: est += [("BACKGROUND", (0, 0), (-1, 0), AZ)]
    for i in range(1, len(linhas)):
        if i % 2 == 0: est.append(("BACKGROUND", (0, i), (-1, i), colors.HexColor("#F2F6FA")))
    t.setStyle(TableStyle(est)); return t

def img(nome, larg_cm, alt_max_cm=None):
    p = os.path.join(HERE, nome)
    w, h = PILImage.open(p).size
    ww = larg_cm * cm; hh = ww * h / w
    if alt_max_cm and hh > alt_max_cm * cm:
        hh = alt_max_cm * cm; ww = hh * w / h
    return Image(p, width=ww, height=hh)

class Doc(BaseDocTemplate):
    def __init__(self, fn):
        super().__init__(fn, pagesize=A4, title="Bairro em Dia - Documentação (Global Solution 2026.2)",
                         author=ALUNO, subject="Global Solution 2026.2 - Domain Driven Design (Java) - FIAP")
        pw, ph = A4
        fp = Frame(2*cm, 2*cm, pw-4*cm, ph-4*cm, id="fp")
        fl = Frame(2*cm, 2*cm, ph-4*cm, pw-4*cm, id="fl")
        self.addPageTemplates([
            PageTemplate(id="cover", frames=[Frame(2*cm, 2*cm, pw-4*cm, ph-4*cm, id="fc")], onPage=self.nada, pagesize=A4),
            PageTemplate(id="portrait", frames=[fp], onPage=self.rodape, pagesize=A4),
            PageTemplate(id="landscape", frames=[fl], onPage=self.rodape, pagesize=landscape(A4)),
        ])
    def nada(self, c, d): pass
    def rodape(self, c, d):
        c.saveState(); c.setFont("DV", 7.5); c.setFillColor(colors.HexColor("#666666"))
        w, h = c._pagesize
        c.drawString(2*cm, 1.1*cm, f"Bairro em Dia - {ALUNO} - RM {RM} - FIAP GS 2026.2")
        c.drawRightString(w - 2*cm, 1.1*cm, f"Página {d.page}"); c.restoreState()
    def afterFlowable(self, f):
        if isinstance(f, Paragraph):
            if f.style.name == "h1":
                self.notify("TOCEntry", (0, f.getPlainText(), self.page))
            elif f.style.name == "h2":
                self.notify("TOCEntry", (1, f.getPlainText(), self.page))

def S():
    s = []
    # ---------------- CAPA ----------------
    s += [Spacer(1, 3.5*cm),
          Paragraph("FIAP — Faculdade de Informática e Administração Paulista", ParagraphStyle("c0", fontName="DV", fontSize=11, alignment=TA_CENTER, textColor=colors.HexColor("#555555"))),
          Spacer(1, 1.2*cm),
          Paragraph("Bairro em Dia", ParagraphStyle("c1", fontName="DV-B", fontSize=34, leading=40, alignment=TA_CENTER, textColor=AZ)),
          Spacer(1, 0.3*cm),
          Paragraph("Plataforma CitizenTech para registro, acompanhamento e priorização de problemas urbanos", ParagraphStyle("c2", fontName="DV", fontSize=13, leading=18, alignment=TA_CENTER)),
          Spacer(1, 1.2*cm),
          Paragraph("Documentação do Projeto — Global Solution 2026.2", ParagraphStyle("c3", fontName="DV-B", fontSize=13, alignment=TA_CENTER)),
          Spacer(1, 3*cm)]
    cap = [["Aluno", ALUNO], ["RM", RM], ["Turma", TURMA], ["Disciplina", "Domain Driven Design — Java"],
           ["Professor", PROF], ["Desenvolvimento", "Individual"], ["Tema", "CitizenTech"]]
    t = Table([[Paragraph(f"<b>{a}</b>", ParagraphStyle("k", fontName="DV-B", fontSize=10.5)), Paragraph(b, ParagraphStyle("v", fontName="DV", fontSize=10.5))] for a, b in cap],
              colWidths=[5*cm, 9*cm])
    t.setStyle(TableStyle([("LINEBELOW", (0, 0), (-1, -1), 0.3, colors.HexColor("#CCCCCC")), ("TOPPADDING", (0,0), (-1,-1), 5), ("BOTTOMPADDING", (0,0), (-1,-1), 5)]))
    s += [t, NextPageTemplate("portrait"), PageBreak()]

    # ---------------- ÍNDICE ----------------
    toc = TableOfContents()
    toc.levelStyles = [ParagraphStyle("t0", fontName="DV-B", fontSize=10, leading=15, leftIndent=0),
                       ParagraphStyle("t1", fontName="DV", fontSize=9, leading=13, leftIndent=14)]
    s += [Paragraph("Índice", ParagraphStyle("ti", fontName="DV-B", fontSize=16, textColor=AZ, spaceAfter=10)), toc, PageBreak()]

    # ---------------- 1 APRESENTAÇÃO ----------------
    s += [H1("1. Apresentação"),
          P("O <b>Bairro em Dia</b> é uma API RESTful desenvolvida em Java com Spring Boot e banco de dados Oracle (servidor FIAP), "
            "criada como entrega da Global Solution 2026.2 da disciplina de Domain Driven Design — Java. A solução pertence ao tema "
            "<b>CitizenTech</b>: tecnologia a serviço do cidadão e da gestão das cidades."),
          P("Este documento reúne a especificação do projeto, os diagramas (classes e DER), os protótipos de interface, a tabela de endpoints, "
            "exemplos de requisições e as instruções de execução, configuração do Oracle e deploy."),
          H1("2. Problema"),
          P("Problemas de infraestrutura urbana — buracos em ruas, postes sem iluminação, calçadas danificadas, acúmulo de lixo — são percebidos primeiro "
            "por quem mora no bairro. Porém, os registros costumam ser dispersos (ligações, redes sociais, conversas), sem padronização, "
            "sem acompanhamento do andamento e sem critério claro de prioridade. O morador não sabe se o problema foi visto, e a equipe de manutenção "
            "não tem uma visão organizada do que é mais urgente."),
          H1("3. Justificativa"),
          P("Centralizar as ocorrências em um sistema único, com categorias, prioridade e status, torna o trabalho de manutenção urbana mais transparente e eficiente. "
            "Para o cidadão, há um canal simples de registro e acompanhamento; para o poder público, há dados estruturados que permitem "
            "priorizar ações. Do ponto de vista acadêmico, o projeto aplica de forma integrada os conteúdos da disciplina: classes e construtores, "
            "exceções, JDBC/DAO, MVC, API REST com Spring, Spring Data JPA com Oracle, Bean Validation, testes de endpoints e deploy."),
          H1("4. Objetivos"),
          H2("Objetivo geral"),
          P("Desenvolver uma API REST que permita registrar, gerenciar, acompanhar e priorizar ocorrências urbanas reportadas por moradores."),
          H2("Objetivos específicos")] + B([
          "Cadastrar moradores, categorias de problemas e ocorrências (CRUD completo com GET, POST, PUT e DELETE).",
          "Controlar o ciclo de vida da ocorrência por status (ABERTA, EM_ANALISE, EM_ANDAMENTO, RESOLVIDA, CANCELADA) com regras de transição.",
          "Permitir a priorização por nível de 1 a 5.",
          "Separar responsabilidades em camadas (Controller, BO, DAO, Model) e tratar erros de forma centralizada.",
          "Persistir os dados no Oracle da FIAP e publicar a aplicação no Render."]) + [PageBreak()]

    # ---------------- 5 SOLUÇÃO ----------------
    s += [H1("5. Descrição da solução"),
          P("A aplicação expõe três recursos: <b>moradores</b>, <b>categorias</b> e <b>ocorrências</b>. Um morador registra uma ocorrência classificada por uma categoria. "
            "A ocorrência nasce com status <b>ABERTA</b> e data de criação definida pelo sistema; a equipe responsável a faz evoluir pelos demais status, "
            "e a prioridade pode ser ajustada enquanto a ocorrência não estiver finalizada."),
          H2("Regras de negócio implementadas (camada BO e entidades)")] + B([
          "Fluxo de status: ABERTA → EM_ANALISE → EM_ANDAMENTO → RESOLVIDA. CANCELADA é possível a partir de qualquer status não final.",
          "Ocorrência RESOLVIDA ou CANCELADA não pode mais ser alterada (HTTP 409).",
          "Transição fora do fluxo (por exemplo ABERTA → RESOLVIDA) é rejeitada com HTTP 409 e mensagem com as transições permitidas.",
          "Prioridade obrigatória, entre 1 e 5 (HTTP 400 se fora da faixa).",
          "Ocorrência EM_ANDAMENTO não pode ser excluída (HTTP 409).",
          "Morador ou categoria com ocorrências vinculadas não podem ser excluídos (HTTP 409).",
          "E-mail do morador e nome da categoria são únicos (HTTP 409 em duplicidade).",
          "O morador autor de uma ocorrência não pode ser trocado no PUT; o morador e a categoria informados precisam existir (HTTP 404)."]) + [
          H1("6. Arquitetura"),
          P("Fluxo de uma requisição: <b>Controller → BO → DAO → Oracle</b>."),
          tabela([["Camada", "Pacote", "Responsabilidade"],
                  ["Controller", "controller", "Recebe a requisição HTTP, valida o JSON (Bean Validation) e delega à BO. Não contém regra de negócio."],
                  ["BO", "bo", "Regras de negócio: transições de status, conflitos, existência de registros relacionados."],
                  ["DAO", "dao / dao.repository", "Acesso a dados. Cada DAO é uma classe @Repository que encapsula um JpaRepository (Spring Data JPA). A BO só conhece o DAO."],
                  ["Model", "model", "Entidades JPA com construtores vazio (JPA) e parametrizado (valida e evita estado inválido) e o enum StatusOcorrencia."],
                  ["DTO", "dto", "Records de entrada (com Bean Validation) e de saída; a entidade não é exposta diretamente."],
                  ["Exception", "exception", "Exceções personalizadas e GlobalExceptionHandler (@RestControllerAdvice)."],
                  ["Config", "config", "Configuração de CORS."]], [2.4, 3.2, 11.4]),
          Spacer(1, 6),
          H2("Decisões arquiteturais")] + B([
          "<b>Spring Data JPA dentro do DAO:</b> mantém o padrão DAO ensinado (Aulas 34–37) e usa o JPA da Aula 44. A BO depende de DAOs, nunca dos repositories.",
          "<b>Sequences em vez de IDENTITY:</b> funcionam em qualquer versão do Oracle, sem depender de recursos do 12c.",
          "<b>Tabelas criadas por script SQL</b> (ddl-auto=none): o Hibernate não altera o banco da FIAP.",
          "<b>Credenciais por variáveis de ambiente</b> (DB_URL, DB_USER, DB_PASSWORD): nada sensível no código ou no GitHub.",
          "<b>Deploy via Docker:</b> o Render não possui runtime Java nativo; o Dockerfile compila com Maven/JDK 21 e executa o JAR com JRE 21.",
          "<b>Sem front-end implementado:</b> o foco foi a API e o deploy (itens pontuados). As telas da seção 10 são protótipos."]) + [PageBreak()]

    # ---------------- 7 TECNOLOGIAS ----------------
    s += [H1("7. Tecnologias"),
          tabela([["Item", "Versão / detalhe"],
                  ["Java (JDK)", "21"],
                  ["Spring Boot", "4.1.2 (linha 4.1.x; o material da Aula 44 cita 4.1.0)"],
                  ["Spring Web MVC", "spring-boot-starter-webmvc (no Spring Boot 4 substitui o antigo starter-web)"],
                  ["Spring Data JPA / Hibernate", "spring-boot-starter-data-jpa (Hibernate 7)"],
                  ["Bean Validation", "spring-boot-starter-validation"],
                  ["Driver Oracle", "ojdbc11 (versão gerenciada pelo Spring Boot)"],
                  ["Banco de dados", "Oracle (servidor FIAP) — scripts compatíveis com Oracle 11g+"],
                  ["Testes", "JUnit 5, Mockito, AssertJ, MockMvc; H2 em memória apenas no perfil de testes"],
                  ["Build", "Maven 3.9+ (JAR executável: target/bairro-em-dia.jar)"],
                  ["Deploy", "Render (Docker)"],
                  ["IDE / ferramentas", "IntelliJ IDEA, Oracle SQL Developer, Insomnia/Postman, Git/GitHub"]], [5.5, 11.5]),
          P("<i>Observação sobre versões:</i> a versão do Spring Boot foi conferida nas publicações oficiais; o Spring Boot 4.1.2 foi lançado em 23/10/2026. "
            "Se preferir exatamente a 4.1.0 do material, basta alterar a versão do parent no pom.xml.", "body"),
          H1("8. Requisitos"),
          H2("Requisitos funcionais"),
          tabela([["ID", "Requisito"],
                  ["RF01", "Cadastrar, listar, consultar, atualizar e excluir moradores."],
                  ["RF02", "Cadastrar, listar, consultar, atualizar e excluir categorias."],
                  ["RF03", "Registrar, listar, consultar, atualizar e excluir ocorrências vinculadas a morador e categoria."],
                  ["RF04", "Alterar o status da ocorrência respeitando o fluxo permitido."],
                  ["RF05", "Alterar a prioridade da ocorrência (1 a 5) enquanto não finalizada."],
                  ["RF06", "Informar erros com código HTTP e mensagem clara (400, 404, 405, 409, 500)."]], [2, 15]),
          Spacer(1, 6), H2("Requisitos não funcionais"),
          tabela([["ID", "Requisito"],
                  ["RNF01", "API REST em JSON, com códigos HTTP adequados (201 em criação, 204 em exclusão)."],
                  ["RNF02", "Persistência no Oracle da FIAP; credenciais somente por variáveis de ambiente."],
                  ["RNF03", "Arquitetura em camadas (Controller → BO → DAO) com responsabilidades separadas."],
                  ["RNF04", "Respostas de erro sem stack trace nem detalhes internos do banco."],
                  ["RNF05", "Aplicação publicada no Render e executável por java -jar."]], [2, 15]),
          PageBreak()]

    # ---------------- 9 ENDPOINTS ----------------
    s += [H1("9. Tabela de endpoints"),
          tabela([["Método", "Rota", "Descrição", "Corpo", "Sucesso", "Erros possíveis"],
                  ["GET", "/", "Status da API (health check)", "—", "200", "—"],
                  ["GET", "/moradores", "Lista moradores", "—", "200", "—"],
                  ["GET", "/moradores/{id}", "Busca morador", "—", "200", "400, 404"],
                  ["POST", "/moradores", "Cria morador", "MoradorRequest", "201", "400, 409"],
                  ["PUT", "/moradores/{id}", "Atualiza morador", "MoradorRequest", "200", "400, 404, 409"],
                  ["DELETE", "/moradores/{id}", "Exclui morador", "—", "204", "404, 409"],
                  ["GET", "/categorias", "Lista categorias", "—", "200", "—"],
                  ["GET", "/categorias/{id}", "Busca categoria", "—", "200", "400, 404"],
                  ["POST", "/categorias", "Cria categoria", "CategoriaRequest", "201", "400, 409"],
                  ["PUT", "/categorias/{id}", "Atualiza categoria", "CategoriaRequest", "200", "400, 404, 409"],
                  ["DELETE", "/categorias/{id}", "Exclui categoria", "—", "204", "404, 409"],
                  ["GET", "/ocorrencias", "Lista ocorrências", "—", "200", "—"],
                  ["GET", "/ocorrencias/{id}", "Busca ocorrência", "—", "200", "400, 404"],
                  ["POST", "/ocorrencias", "Cria ocorrência (status ABERTA)", "OcorrenciaRequest", "201", "400, 404"],
                  ["PUT", "/ocorrencias/{id}", "Atualiza ocorrência", "OcorrenciaUpdateRequest", "200", "400, 404, 409"],
                  ["PATCH", "/ocorrencias/{id}/status", "Altera só o status", "StatusRequest", "200", "400, 404, 409"],
                  ["PATCH", "/ocorrencias/{id}/prioridade", "Altera só a prioridade", "PrioridadeRequest", "200", "400, 404, 409"],
                  ["DELETE", "/ocorrencias/{id}", "Exclui ocorrência", "—", "204", "404, 409"]],
                 [1.5, 4.0, 4.0, 3.2, 1.5, 2.8]),
          Spacer(1, 8),
          P("As operações GET, POST, PUT e DELETE estão implementadas para os três recursos; os dois PATCH são complementos para atualização parcial de status e prioridade."),
          PageBreak()]

    # ---------------- 10 PROTÓTIPOS ----------------
    s += [H1("10. Protótipos de telas"),
          P("<b>Importante:</b> as telas abaixo são <b>protótipos</b> da experiência proposta ao usuário (inspirados nas Aulas 38–41 e 47–48). "
            "<b>Nenhuma delas está implementada</b> neste projeto. A interface efetivamente implementada é a <b>API REST</b>, testada via Insomnia/Postman e acessível pelo "
            "deploy. Os protótipos mostram como um front-end (por exemplo, Thymeleaf) consumiria os endpoints da seção 9."),
          img("proto_1_lista.png", 15.5, 10.5), P("Figura 1 — Protótipo: lista de ocorrências (GET /ocorrencias).", "cap"),
          img("proto_2_nova.png", 15.5, 10.5), P("Figura 2 — Protótipo: registro de nova ocorrência (POST /ocorrencias).", "cap"),
          PageBreak(),
          img("proto_3_detalhe.png", 15.5, 10.5), P("Figura 3 — Protótipo: detalhe e acompanhamento (GET /ocorrencias/{id}, PATCH status/prioridade, PUT, DELETE).", "cap"),
          img("proto_4_cadastros.png", 15.5, 10.5), P("Figura 4 — Protótipo: cadastros de moradores e categorias (POST/PUT/DELETE).", "cap"),
          tabela([["Tela (protótipo)", "Endpoints utilizados", "Situação"],
                  ["Lista de ocorrências", "GET /ocorrencias, GET /categorias", "Protótipo (não implementada)"],
                  ["Nova ocorrência", "POST /ocorrencias, GET /moradores, GET /categorias", "Protótipo (não implementada)"],
                  ["Detalhe da ocorrência", "GET/PUT/DELETE /ocorrencias/{id}, PATCH …/status, PATCH …/prioridade", "Protótipo (não implementada)"],
                  ["Cadastros", "POST/PUT/DELETE /moradores e /categorias", "Protótipo (não implementada)"],
                  ["API REST (JSON)", "Todos os endpoints da seção 9", "<b>Implementada</b>"]], [4, 8.5, 4.5]),
          NextPageTemplate("portrait"), PageBreak()]

    # ---------------- 11 CLASSES ----------------
    s += [H1("11. Diagrama de classes"),
          P("Os diagramas refletem as classes existentes no código-fonte. Para legibilidade, foram divididos em vistas: domínio (model), "
            "controller/BO, BO/DAO/repositories, exceções e DTOs. Ficam fora dos diagramas apenas a classe principal "
            "<i>BairroEmDiaApplication</i>, a configuração <i>CorsConfig</i>, a classe utilitária package-private <i>Validacao</i> e o <i>StatusController</i> "
            "(que aparece na vista de controllers)."),
          H2("11.1 Domínio (model)"), img("classes_dominio.png", 15.5, 19),
          P("Figura 5 — Morador 1:N Ocorrencia; Categoria 1:N Ocorrencia; Ocorrencia possui um StatusOcorrencia.", "cap"),
          PageBreak(),
          H2("11.2 Controllers e BOs"), img("classes_camadas_a.png", 16, 14), P("Figura 6 — Controller → BO.", "cap"),
          NextPageTemplate("landscape"), PageBreak(),
          H2("11.3 BOs, DAOs e Repositories"), img("classes_camadas_b.png", 24.5, 14.5),
          P("Figura 7 — BO → DAO → Repository (Spring Data JPA encapsulado no DAO).", "cap"),
          PageBreak(),
          H2("11.4 Exceções"), img("classes_excecoes_dto.png", 24.5, 14.5),
          P("Figura 8 — Exceções personalizadas, tratamento centralizado e corpo de erro.", "cap"),
          NextPageTemplate("portrait"), PageBreak(),
          H2("11.5 DTOs"), img("classes_dto.png", 11.5, 21),
          P("Figura 9 — DTOs de entrada/saída (records) e conversão a partir das entidades.", "cap"),
          NextPageTemplate("landscape"), PageBreak()]

    # ---------------- 12 DER ----------------
    s += [H1("12. Diagrama Entidade-Relacionamento (DER)"),
          img("der.png", 24.5, 12.5),
          P("Figura 10 — Tabelas do Oracle (idênticas ao script sql/01_create_tables.sql). PK: chave primária; FK: chave estrangeira; UK: única.", "cap"),
          NextPageTemplate("portrait"), PageBreak()]

    s += [H2("Script de criação (resumo)"),
          code("""
CREATE SEQUENCE SEQ_MORADOR START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;   -- idem SEQ_CATEGORIA, SEQ_OCORRENCIA

CREATE TABLE TB_MORADOR (
  ID_MORADOR NUMBER(19) NOT NULL, NOME VARCHAR2(100) NOT NULL, EMAIL VARCHAR2(150) NOT NULL,
  CONSTRAINT PK_MORADOR PRIMARY KEY (ID_MORADOR), CONSTRAINT UK_MORADOR_EMAIL UNIQUE (EMAIL));

CREATE TABLE TB_CATEGORIA (
  ID_CATEGORIA NUMBER(19) NOT NULL, NOME VARCHAR2(80) NOT NULL, DESCRICAO VARCHAR2(255),
  CONSTRAINT PK_CATEGORIA PRIMARY KEY (ID_CATEGORIA), CONSTRAINT UK_CATEGORIA_NOME UNIQUE (NOME));

CREATE TABLE TB_OCORRENCIA (
  ID_OCORRENCIA NUMBER(19) NOT NULL, TITULO VARCHAR2(120) NOT NULL, DESCRICAO VARCHAR2(1000) NOT NULL,
  LOCALIZACAO VARCHAR2(255) NOT NULL, PRIORIDADE NUMBER(1) NOT NULL, STATUS_OCORRENCIA VARCHAR2(20) NOT NULL,
  DATA_CRIACAO TIMESTAMP NOT NULL, ID_MORADOR NUMBER(19) NOT NULL, ID_CATEGORIA NUMBER(19) NOT NULL,
  CONSTRAINT PK_OCORRENCIA PRIMARY KEY (ID_OCORRENCIA),
  CONSTRAINT FK_OCORRENCIA_MORADOR   FOREIGN KEY (ID_MORADOR)   REFERENCES TB_MORADOR (ID_MORADOR),
  CONSTRAINT FK_OCORRENCIA_CATEGORIA FOREIGN KEY (ID_CATEGORIA) REFERENCES TB_CATEGORIA (ID_CATEGORIA),
  CONSTRAINT CK_OCORRENCIA_PRIORIDADE CHECK (PRIORIDADE BETWEEN 1 AND 5),
  CONSTRAINT CK_OCORRENCIA_STATUS CHECK (STATUS_OCORRENCIA IN
     ('ABERTA','EM_ANALISE','EM_ANDAMENTO','RESOLVIDA','CANCELADA')));
"""),
          P("O script completo (com índices) está em <i>sql/01_create_tables.sql</i>; dados de exemplo em <i>sql/02_seed.sql</i>.")]

    # ---------------- 13 JSON ----------------
    s += [H1("13. Exemplos JSON"),
          P("Os exemplos abaixo mostram o <b>formato</b> das requisições e respostas (valores ilustrativos, não são evidências de execução)."),
          H2("POST /moradores"),
          code('''
Requisição:                                   Resposta 201 Created:
{                                             {
  "nome": "Maria da Silva",                     "id": 1,
  "email": "maria@example.com"                  "nome": "Maria da Silva",
}                                               "email": "maria@example.com"
                                              }
'''),
          H2("POST /categorias"),
          code('''
Requisição:                                   Resposta 201 Created:
{                                             {
  "nome": "Buraco na rua",                      "id": 1,
  "descricao": "Buracos no asfalto"             "nome": "Buraco na rua",
}                                               "descricao": "Buracos no asfalto"
                                              }
'''),
          H2("POST /ocorrencias"),
          code('''
Requisição:
{
  "titulo": "Buraco na Rua das Flores",
  "descricao": "Buraco grande em frente ao número 120",
  "localizacao": "Rua das Flores, 120",
  "prioridade": 4,
  "moradorId": 1,
  "categoriaId": 1
}
Resposta 201 Created:
{
  "id": 1, "titulo": "Buraco na Rua das Flores", "descricao": "Buraco grande em frente ao número 120",
  "localizacao": "Rua das Flores, 120", "prioridade": 4, "status": "ABERTA",
  "dataCriacao": "2026-11-18T10:30:00", "moradorId": 1, "moradorNome": "Maria da Silva",
  "categoriaId": 1, "categoriaNome": "Buraco na rua"
}
'''),
          H2("PUT /ocorrencias/{id}  e  PATCH /ocorrencias/{id}/status  e  /prioridade"),
          code('''
PUT:    { "titulo": "...", "descricao": "...", "localizacao": "...", "prioridade": 5,
          "status": "EM_ANALISE", "categoriaId": 1 }
PATCH /status:      { "status": "EM_ANDAMENTO" }
PATCH /prioridade:  { "prioridade": 3 }
'''),
          H2("Respostas de erro (formato)"),
          code('''
400 Bad Request  (POST /moradores com nome vazio)
{ "timestamp": "...", "status": 400, "erro": "Bad Request", "mensagem": "Dados inválidos na requisição.",
  "caminho": "/moradores", "detalhes": ["nome: O nome é obrigatório"] }

404 Not Found    (GET /moradores/9999)
{ "status": 404, "erro": "Not Found", "mensagem": "Morador com id 9999 não encontrado(a).",
  "caminho": "/moradores/9999", "detalhes": [] }

409 Conflict     (PATCH /ocorrencias/1/status com ABERTA -> RESOLVIDA)
{ "status": 409, "erro": "Conflict", "mensagem": "Transição de status inválida: ABERTA -> RESOLVIDA. ...",
  "caminho": "/ocorrencias/1/status", "detalhes": [] }
'''),
          PageBreak()]

    # ---------------- 14 EXECUÇÃO ----------------
    s += [H1("14. Instruções de execução"),
          H2("Ferramentas e versões necessárias"),
          tabela([["Ferramenta", "Versão / observação"],
                  ["JDK", "21"], ["Maven", "3.9+ (ou o Maven embutido do IntelliJ IDEA)"],
                  ["Spring Boot", "4.1.2 (definido no pom.xml)"], ["Banco", "Oracle FIAP (acesso pelo Oracle SQL Developer)"],
                  ["Driver", "ojdbc11 (dependência do pom.xml, baixada automaticamente pelo Maven)"],
                  ["Dependências", "webmvc, data-jpa, validation, ojdbc11, test, h2 (somente testes)"],
                  ["Git", "Para versionamento e deploy via GitHub"]], [4, 13]),
          H2("Passo a passo local")] + B([
          "Abra o Oracle SQL Developer, conecte-se ao banco da FIAP e execute <i>sql/01_create_tables.sql</i> (opcional: <i>sql/02_seed.sql</i>).",
          "Abra a pasta do projeto no IntelliJ IDEA (JDK 21) e aguarde o Maven baixar as dependências.",
          "Defina as variáveis de ambiente DB_URL, DB_USER e DB_PASSWORD na configuração de execução (Run &gt; Edit Configurations &gt; Environment variables).",
          "Execute a classe <i>BairroEmDiaApplication</i>, ou gere e rode o JAR conforme abaixo.",
          "Acesse http://localhost:8080/ e teste os endpoints com o Insomnia/Postman (pasta <i>http/</i>)."]) + [
          code('''
mvn clean -DskipTests package          # gera target/bairro-em-dia.jar
java -jar target/bairro-em-dia.jar     # executa (variáveis DB_URL, DB_USER, DB_PASSWORD definidas)
mvn test                               # executa os testes automatizados
'''),
          H2("Configuração necessária (application.properties)"),
          code('''
server.port=${PORT:8080}
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=none      # tabelas criadas pelos scripts SQL
'''),
          H1("15. Configuração do Oracle"),
          P("Os dados de conexão (host, porta, service name/SID, usuário e senha) <b>não constam no código nem neste documento</b>; devem ser os mesmos da conexão criada no Oracle SQL Developer. "
            "O formato da URL JDBC depende de a conexão usar <i>service name</i> ou <i>SID</i>:"),
          code('''
DB_URL=jdbc:oracle:thin:@<HOST>:<PORTA>/<SERVICE_NAME>      (service name)
DB_URL=jdbc:oracle:thin:@<HOST>:<PORTA>:<SID>               (SID)
DB_USER=<usuário Oracle FIAP>
DB_PASSWORD=<senha Oracle FIAP>
'''),
          P("Os scripts usam <i>SEQUENCE</i> para os identificadores, o que dispensa recursos do Oracle 12c+ (IDENTITY). As entidades JPA mapeiam exatamente as tabelas e colunas do script."),
          PageBreak()]

    # ---------------- 16 DEPLOY ----------------
    s += [H1("16. Deploy no Render"),
          P("O Render não possui ambiente Java nativo; a publicação usa o <b>Dockerfile</b> do projeto (build com Maven + JDK 21 e execução com JRE 21). "
            "O comando equivalente ao da Aula 49 é executado dentro do Dockerfile: <i>mvn clean -DskipTests package</i> e <i>java -jar app.jar</i>."),
          H2("Configuração do serviço")] + B([
          "Tipo: <b>Web Service</b> conectado ao repositório GitHub; Runtime/Language: <b>Docker</b>; plano Free.",
          "Variáveis de ambiente: <b>DB_URL</b>, <b>DB_USER</b>, <b>DB_PASSWORD</b> (a variável PORT é definida pelo próprio Render e lida em server.port).",
          "Health Check Path: <b>/</b> (rota que não acessa o banco).",
          "Arquivo opcional <i>render.yaml</i> (Blueprint) incluído no projeto."]) + [
          H2("Testes da URL pública (após o deploy)")] + B([
          "GET https://&lt;servico&gt;.onrender.com/ → deve retornar JSON com status \"online\".",
          "GET /categorias e /moradores → listas (podem estar vazias) com HTTP 200; isso comprova a conexão com o Oracle.",
          "POST /moradores, POST /categorias e POST /ocorrencias → 201; depois GET, PUT, PATCH e DELETE."]) + [
          H2("Diagnóstico de possíveis erros"),
          tabela([["Sintoma", "Causa provável", "O que fazer"],
                  ["Build falha no Render", "Dependência/versão ou erro de compilação", "Ver o log do build; rodar mvn clean package localmente."],
                  ["Serviço reinicia ou fica 'Failed'", "Variáveis DB_URL/DB_USER/DB_PASSWORD ausentes (Could not resolve placeholder)", "Cadastrar as três variáveis em Environment."],
                  ["/ responde, mas /moradores dá 500", "Sem conexão com o Oracle (host/porta/serviço errados, firewall, credenciais)", "Conferir os logs; validar os dados no SQL Developer; verificar se o servidor aceita conexões externas."],
                  ["ORA-00942 (table or view does not exist)", "Scripts SQL não executados no schema do usuário", "Executar sql/01_create_tables.sql com o mesmo usuário usado em DB_USER."],
                  ["Primeira requisição muito lenta", "Plano free 'adormece' o serviço", "Aguardar cerca de 1 minuto e repetir."]], [4.2, 6, 6.8]),
          P("<b>Risco principal:</b> o Render precisa alcançar o Oracle da FIAP pela internet. Isso só pode ser confirmado testando a aplicação publicada; "
            "o deploy não deve ser considerado concluído apenas porque a URL foi criada.", "warn"),
          PageBreak()]

    # ---------------- 17 EVIDÊNCIAS ----------------
    s += [H1("17. Evidências dos testes"),
          P("Foram escritos <b>43 métodos de teste</b> automatizados (JUnit 5, Mockito e MockMvc):"),
          tabela([["Classe de teste", "Qtde", "O que cobre"],
                  ["BairroEmDiaApplicationTests", "1", "Inicialização do contexto Spring"],
                  ["StatusOcorrenciaTest", "4", "Regras de transição de status e validações das entidades (construtores)"],
                  ["OcorrenciaBoTest", "7", "Regras de negócio da BO com DAOs simulados (Mockito)"],
                  ["MoradorApiTest", "11", "CRUD, validação (400), duplicidade (409), inexistente (404), exclusão com vínculos (409), JSON inválido"],
                  ["CategoriaApiTest", "4", "CRUD, validação, duplicidade e exclusão com vínculos"],
                  ["OcorrenciaApiTest", "14", "CRUD, relacionamentos, status, prioridade, regras 400/404/409, rota raiz"],
                  ["OracleConexaoTest", "2", "Conexão e consulta no Oracle real (só executa com DB_URL definida)"]], [5.2, 1.4, 10.4])]
    ev = os.path.join(HERE, "evidencias.txt")
    if os.path.exists(ev):
        txt = open(ev, encoding="utf-8").read().strip()
        s += [Spacer(1, 6), H2("Saída real da execução"), Preformatted(txt[:6000], st["code"])]
    else:
        s += [Spacer(1, 6),
              P("<b>Evidências de execução ainda não anexadas.</b> Os testes foram preparados, mas a saída de execução (<i>mvn test</i>, testes do Oracle e testes da URL pública no Render) "
                "só pode ser registrada depois de executada no ambiente do aluno. Nenhum resultado foi inventado neste documento.", "warn")]

    s += [H1("18. GitHub e Render"),
          tabela([["Item", "Link"],
                  ["Repositório GitHub", link_txt(GITHUB, "link do repositório GitHub")],
                  ["Deploy no Render", link_txt(RENDER, "link do serviço no Render")]], [4.5, 12.5]),
          Spacer(1, 6),
          H1("19. Conclusão"),
          P("O Bairro em Dia entrega uma API REST organizada em camadas, com modelo de domínio consistente (Morador, Categoria e Ocorrência), regras de negócio explícitas para "
            "status e prioridade, tratamento centralizado de exceções com respostas HTTP adequadas, persistência em Oracle via Spring Data JPA encapsulado em DAOs e preparação para deploy no Render. "
            "A documentação reúne diagramas fiéis ao código, tabela de endpoints, protótipos (claramente diferenciados do que foi implementado) e instruções reproduzíveis."),
          P("Como evolução, o projeto pode ganhar uma interface web (por exemplo, com Thymeleaf), autenticação de usuários, filtros e paginação nas listagens, "
            "geolocalização das ocorrências e painéis de acompanhamento para a gestão pública.")]
    return s

if __name__ == "__main__":
    out = sys.argv[1] if len(sys.argv) > 1 else os.path.join(HERE, "Documentacao_Bairro_em_Dia.pdf")
    doc = Doc(out)
    doc.multiBuild(S())
    print("PDF gerado:", out)
